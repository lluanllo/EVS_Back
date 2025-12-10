package com.empresa.course.MicroserviceCourseApi.Service.Impl;

import com.empresa.course.MicroserviceCourseApi.Entities.Equipment;
import com.empresa.course.MicroserviceCourseApi.Entities.Enums.EquipmentStatus;
import com.empresa.course.MicroserviceCourseApi.Entities.Enums.EquipmentType;
import com.empresa.course.MicroserviceCourseApi.Entities.Rental;
import com.empresa.course.MicroserviceCourseApi.Repository.EquipmentRepository;
import com.empresa.course.MicroserviceCourseApi.Repository.RentalRepository;
import com.empresa.course.MicroserviceCourseApi.Service.Inter.IRentalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalService implements IRentalService {

    private final EquipmentRepository equipmentRepository;
    private final RentalRepository rentalRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // ===== Equipment CRUD =====

    @Override
    @Transactional
    public Equipment createEquipment(Equipment equipment) {
        equipment.setStatus(EquipmentStatus.DISPONIBLE);
        Equipment saved = equipmentRepository.save(equipment);
        log.info("Equipamiento creado: {} - {}", saved.getId(), saved.getName());
        return saved;
    }

    @Override
    @Transactional
    public Equipment updateEquipment(Long id, Equipment equipment) {
        Equipment existing = getEquipmentById(id);
        existing.setName(equipment.getName());
        existing.setDescription(equipment.getDescription());
        existing.setEquipmentType(equipment.getEquipmentType());
        existing.setPricePerHour(equipment.getPricePerHour());
        existing.setPricePerDay(equipment.getPricePerDay());
        existing.setMinSkillLevel(equipment.getMinSkillLevel());
        existing.setNotes(equipment.getNotes());
        existing.setMemberOnly(equipment.getMemberOnly());
        return equipmentRepository.save(existing);
    }

    @Override
    public Equipment getEquipmentById(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipamiento no encontrado: " + id));
    }

    @Override
    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    @Override
    public List<Equipment> getAvailableEquipment() {
        return equipmentRepository.findByStatus(EquipmentStatus.DISPONIBLE);
    }

    @Override
    public List<Equipment> getAvailableEquipmentByType(EquipmentType type) {
        return equipmentRepository.findByEquipmentTypeAndStatus(type, EquipmentStatus.DISPONIBLE);
    }

    @Override
    @Transactional
    public void deleteEquipment(Long id) {
        if (!equipmentRepository.existsById(id)) {
            throw new RuntimeException("Equipamiento no encontrado: " + id);
        }
        equipmentRepository.deleteById(id);
        log.info("Equipamiento eliminado: {}", id);
    }

    @Override
    @Transactional
    public Equipment setEquipmentMaintenance(Long id) {
        Equipment equipment = getEquipmentById(id);
        equipment.setStatus(EquipmentStatus.EN_MANTENIMIENTO);
        Equipment saved = equipmentRepository.save(equipment);
        log.info("Equipamiento en mantenimiento: {}", id);
        return saved;
    }

    // ===== Rental Operations =====

    @Override
    @Transactional
    public Rental createRental(Long equipmentId, Long studentId, LocalDateTime startTime,
                                LocalDateTime endTime, Long registeredBy) {
        Equipment equipment = getEquipmentById(equipmentId);

        if (equipment.getStatus() != EquipmentStatus.DISPONIBLE) {
            throw new RuntimeException("El equipamiento no está disponible para alquiler");
        }

        // Verificar que no hay alquileres activos para este equipo
        List<Rental> activeRentals = rentalRepository.findActiveByEquipment(equipmentId);
        if (!activeRentals.isEmpty()) {
            throw new RuntimeException("El equipamiento ya está alquilado");
        }

        // Calcular precio
        long hours = Duration.between(startTime, endTime).toHours();
        BigDecimal totalPrice;
        if (hours >= 24 && equipment.getPricePerDay() != null) {
            long days = hours / 24;
            totalPrice = equipment.getPricePerDay().multiply(BigDecimal.valueOf(days));
        } else {
            totalPrice = equipment.getPricePerHour().multiply(BigDecimal.valueOf(Math.max(1, hours)));
        }

        Rental rental = Rental.builder()
                .equipment(equipment)
                .studentId(studentId)
                .startTime(startTime)
                .expectedEndTime(endTime)
                .status("ACTIVO")
                .totalPrice(totalPrice)
                .paid(false)
                .skillVerified(false)
                .registeredBy(registeredBy)
                .build();

        // Actualizar estado del equipo
        equipment.setStatus(EquipmentStatus.ALQUILADO);
        equipmentRepository.save(equipment);

        Rental saved = rentalRepository.save(rental);
        log.info("Alquiler creado: {} - Equipo {} para estudiante {}", saved.getId(), equipmentId, studentId);

        // Publicar evento
        kafkaTemplate.send("rental-events", "rental-created", Map.of(
                "rentalId", saved.getId(),
                "equipmentId", equipmentId,
                "studentId", studentId,
                "totalPrice", totalPrice
        ));

        return saved;
    }

    @Override
    @Transactional
    public Rental completeRental(Long rentalId, String returnCondition, String damageReported) {
        Rental rental = getRentalById(rentalId);
        rental.setActualEndTime(LocalDateTime.now());
        rental.setStatus("COMPLETADO");
        rental.setReturnCondition(returnCondition);
        rental.setDamageReported(damageReported);

        // Liberar equipo
        Equipment equipment = rental.getEquipment();
        if (damageReported != null && !damageReported.isEmpty()) {
            equipment.setStatus(EquipmentStatus.DAÑADO);
            equipment.setNotes((equipment.getNotes() != null ? equipment.getNotes() + "\n" : "")
                    + "Daño reportado: " + damageReported);
        } else {
            equipment.setStatus(EquipmentStatus.DISPONIBLE);
        }
        equipmentRepository.save(equipment);

        Rental saved = rentalRepository.save(rental);
        log.info("Alquiler completado: {}", rentalId);

        // Publicar evento para contabilidad
        kafkaTemplate.send("rental-payment-events", "rental-completed", Map.of(
                "rentalId", saved.getId(),
                "studentId", rental.getStudentId(),
                "totalPrice", rental.getTotalPrice(),
                "paid", rental.getPaid()
        ));

        return saved;
    }

    @Override
    @Transactional
    public Rental cancelRental(Long rentalId) {
        Rental rental = getRentalById(rentalId);
        rental.setStatus("CANCELADO");
        rental.setActualEndTime(LocalDateTime.now());

        // Liberar equipo
        Equipment equipment = rental.getEquipment();
        equipment.setStatus(EquipmentStatus.DISPONIBLE);
        equipmentRepository.save(equipment);

        Rental saved = rentalRepository.save(rental);
        log.info("Alquiler cancelado: {}", rentalId);

        return saved;
    }

    @Override
    public Rental getRentalById(Long id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alquiler no encontrado: " + id));
    }

    @Override
    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    @Override
    public List<Rental> getActiveRentals() {
        return rentalRepository.findByStatus("ACTIVO");
    }

    @Override
    public List<Rental> getRentalsByStudent(Long studentId) {
        return rentalRepository.findByStudentId(studentId);
    }

    @Override
    public List<Rental> getActiveRentalsByStudent(Long studentId) {
        return rentalRepository.findActiveByStudent(studentId);
    }

    @Override
    public List<Rental> getOverdueRentals() {
        return rentalRepository.findOverdueRentals(LocalDateTime.now());
    }

    @Override
    public List<Rental> getUnpaidCompletedRentals() {
        return rentalRepository.findByPaidFalseAndStatus("COMPLETADO");
    }

    // ===== Aptitude Verification =====

    @Override
    public boolean verifyStudentAptitude(Long studentId, Long equipmentId) {
        Equipment equipment = getEquipmentById(equipmentId);
        String minLevel = equipment.getMinSkillLevel();

        if (minLevel == null || minLevel.isEmpty()) {
            return true; // Sin requisito de nivel
        }

        // TODO: Consultar nivel del estudiante via Kafka
        // Por ahora devolvemos true
        log.info("Verificando aptitud del estudiante {} para equipo {}", studentId, equipmentId);
        return true;
    }

    @Override
    @Transactional
    public Rental markAptitudeVerified(Long rentalId, Long verifiedBy) {
        Rental rental = getRentalById(rentalId);
        rental.setSkillVerified(true);
        rental.setVerifiedBy(verifiedBy);
        return rentalRepository.save(rental);
    }
}

