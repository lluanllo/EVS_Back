package com.evs.MicroserviceRegattaApi.Service.Impl;

import com.evs.MicroserviceRegattaApi.Entities.Regatta;
import com.evs.MicroserviceRegattaApi.Repository.RegattaRepository;
import com.evs.MicroserviceRegattaApi.Service.Inter.IClassificationService;
import com.evs.MicroserviceRegattaApi.Service.Inter.IRegattaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegattaServiceImpl implements IRegattaService {

    private final RegattaRepository regattaRepository;
    private final IClassificationService classificationService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public Regatta create(Regatta regatta, Long createdBy) {
        regatta.setCreatedBy(createdBy);
        regatta.setCreatedAt(LocalDateTime.now());
        regatta.setStatus("PLANIFICADA");

        Regatta saved = regattaRepository.save(regatta);
        log.info("Regata creada: {} por usuario {}", saved.getId(), createdBy);

        kafkaTemplate.send("regatta-events", "regatta-created", Map.of(
                "regattaId", saved.getId(),
                "name", saved.getName(),
                "date", saved.getEventDate().toString()
        ));

        return saved;
    }

    @Override
    @Transactional
    public Regatta update(Long id, Regatta regatta) {
        Regatta existing = findById(id);
        existing.setName(regatta.getName());
        existing.setDescription(regatta.getDescription());
        existing.setEventDate(regatta.getEventDate());
        existing.setRegistrationDeadline(regatta.getRegistrationDeadline());
        existing.setLocation(regatta.getLocation());
        existing.setMaxParticipants(regatta.getMaxParticipants());
        existing.setRegistrationFee(regatta.getRegistrationFee());
        existing.setPlannedRaces(regatta.getPlannedRaces());
        existing.setNotes(regatta.getNotes());
        return regattaRepository.save(existing);
    }

    @Override
    public Regatta findById(Long id) {
        return regattaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Regata no encontrada: " + id));
    }

    @Override
    public List<Regatta> findAll() {
        return regattaRepository.findAll();
    }

    @Override
    public List<Regatta> findUpcoming() {
        return regattaRepository.findByEventDateAfterOrderByEventDateAsc(LocalDate.now());
    }

    @Override
    public List<Regatta> findByStatus(String status) {
        return regattaRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Regatta regatta = findById(id);
        if (!"PLANIFICADA".equals(regatta.getStatus())) {
            throw new RuntimeException("Solo se pueden eliminar regatas planificadas");
        }
        regattaRepository.deleteById(id);
        log.info("Regata eliminada: {}", id);
    }

    @Override
    @Transactional
    public Regatta openRegistrations(Long regattaId) {
        Regatta regatta = findById(regattaId);
        regatta.setStatus("INSCRIPCIONES_ABIERTAS");
        log.info("Inscripciones abiertas para regata: {}", regattaId);
        return regattaRepository.save(regatta);
    }

    @Override
    @Transactional
    public Regatta closeRegistrations(Long regattaId) {
        Regatta regatta = findById(regattaId);
        regatta.setStatus("INSCRIPCIONES_CERRADAS");
        log.info("Inscripciones cerradas para regata: {}", regattaId);
        return regattaRepository.save(regatta);
    }

    @Override
    @Transactional
    public Regatta start(Long regattaId) {
        Regatta regatta = findById(regattaId);
        regatta.setStatus("EN_CURSO");
        log.info("Regata iniciada: {}", regattaId);
        return regattaRepository.save(regatta);
    }

    @Override
    @Transactional
    public Regatta finish(Long regattaId) {
        Regatta regatta = findById(regattaId);
        classificationService.calculateFinalClassification(regattaId);
        regatta.setStatus("FINALIZADA");
        log.info("Regata finalizada: {}", regattaId);
        return regattaRepository.save(regatta);
    }

    @Override
    @Transactional
    public Regatta assignStaff(Long regattaId, List<Long> staffIds) {
        Regatta regatta = findById(regattaId);
        regatta.setStaffIds(staffIds);
        log.info("Personal asignado a regata {}: {} personas", regattaId, staffIds.size());
        return regattaRepository.save(regatta);
    }
}

