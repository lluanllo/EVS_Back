package com.evs.Contability.MicroserviceContabilityApi.Service.Impl;

import com.evs.Contability.MicroserviceContabilityApi.DTO.WorkedHoursDTO;
import com.evs.Contability.MicroserviceContabilityApi.Entities.WorkedHours;
import com.evs.Contability.MicroserviceContabilityApi.Repository.WorkedHoursRepository;
import com.evs.Contability.MicroserviceContabilityApi.Service.Inter.IWorkedHoursService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkedHoursServiceImpl implements IWorkedHoursService {

    private final WorkedHoursRepository workedHoursRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public WorkedHours registerHours(WorkedHoursDTO dto) {
        WorkedHours hours = WorkedHours.builder()
                .teacherId(dto.getTeacherId())
                .date(dto.getDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .courseId(dto.getCourseId())
                .activityType(dto.getActivityType())
                .notes(dto.getNotes())
                .validated(false)
                .paid(false)
                .build();

        WorkedHours saved = workedHoursRepository.save(hours);
        log.info("Horas registradas: profesor {} - {} minutos el {}",
                dto.getTeacherId(), saved.getDurationMinutes(), dto.getDate());

        // Notificar al microservicio de profesores
        kafkaTemplate.send("worked-hours-events", "hours-registered", saved);

        return saved;
    }

    @Override
    @Transactional
    public WorkedHours updateHours(Long id, WorkedHoursDTO dto) {
        WorkedHours hours = getById(id);

        if (hours.getValidated()) {
            throw new RuntimeException("No se pueden modificar horas ya validadas");
        }

        hours.setDate(dto.getDate());
        hours.setStartTime(dto.getStartTime());
        hours.setEndTime(dto.getEndTime());
        hours.setCourseId(dto.getCourseId());
        hours.setActivityType(dto.getActivityType());
        hours.setNotes(dto.getNotes());

        return workedHoursRepository.save(hours);
    }

    @Override
    public WorkedHours getById(Long id) {
        return workedHoursRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de horas no encontrado: " + id));
    }

    @Override
    public List<WorkedHours> getAllWorkedHours() {
        return workedHoursRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteWorkedHours(Long id) {
        WorkedHours hours = getById(id);
        if (hours.getValidated()) {
            throw new RuntimeException("No se pueden eliminar horas ya validadas");
        }
        workedHoursRepository.deleteById(id);
        log.info("Registro de horas eliminado: {}", id);
    }

    @Override
    public List<WorkedHours> getByTeacherId(Long teacherId) {
        return workedHoursRepository.findByTeacherId(teacherId);
    }

    @Override
    public List<WorkedHours> getByTeacherIdAndDateRange(Long teacherId, LocalDate start, LocalDate end) {
        return workedHoursRepository.findByTeacherIdAndDateRange(teacherId, start, end);
    }

    @Override
    public List<WorkedHours> getPendingValidation() {
        return workedHoursRepository.findByValidated(false);
    }

    @Override
    @Transactional
    public WorkedHours validateHours(Long id, Long validatedBy) {
        WorkedHours hours = getById(id);
        hours.setValidated(true);
        hours.setValidatedBy(validatedBy);

        WorkedHours saved = workedHoursRepository.save(hours);
        log.info("Horas validadas: {} por usuario {}", id, validatedBy);

        kafkaTemplate.send("worked-hours-events", "hours-validated", saved);

        return saved;
    }

    @Override
    @Transactional
    public void validateMultiple(List<Long> ids, Long validatedBy) {
        ids.forEach(id -> validateHours(id, validatedBy));
        log.info("Validación masiva: {} registros por usuario {}", ids.size(), validatedBy);
    }

    @Override
    public Integer getTotalMinutesByTeacherAndMonth(Long teacherId, Integer month, Integer year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        Integer total = workedHoursRepository.sumValidatedMinutesByTeacherAndDateRange(teacherId, start, end);
        return total != null ? total : 0;
    }

    @Override
    public List<WorkedHours> getUnpaidValidatedByTeacher(Long teacherId) {
        return workedHoursRepository.findUnpaidValidatedByTeacher(teacherId);
    }

    @Override
    @Transactional
    public void markAsPaid(List<Long> ids, Long payrollId) {
        ids.forEach(id -> {
            WorkedHours hours = getById(id);
            hours.setPaid(true);
            hours.setPayrollId(payrollId);
            workedHoursRepository.save(hours);
        });
        log.info("Marcadas como pagadas {} horas en nómina {}", ids.size(), payrollId);
    }
}

