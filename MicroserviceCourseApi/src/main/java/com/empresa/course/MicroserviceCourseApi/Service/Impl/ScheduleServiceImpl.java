package com.empresa.course.MicroserviceCourseApi.Service.Impl;

import com.empresa.course.MicroserviceCourseApi.Entities.Schedule;
import com.empresa.course.MicroserviceCourseApi.Kafka.Events.ScheduleEvent;
import com.empresa.course.MicroserviceCourseApi.Kafka.Producer.CourseEventProducer;
import com.empresa.course.MicroserviceCourseApi.Repository.ScheduleRepository;
import com.empresa.course.MicroserviceCourseApi.Service.Inter.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CourseEventProducer eventProducer;

    @Override
    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    @Override
    public Schedule findById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado: " + id));
    }

    @Override
    public List<Schedule> findByCourseId(Long courseId) {
        return scheduleRepository.findByCourseId(courseId);
    }

    @Override
    public List<Schedule> findByTeacherId(Long teacherId) {
        return scheduleRepository.findByTeacherId(teacherId);
    }

    @Override
    @Transactional
    public Schedule create(Schedule schedule) {
        Schedule saved = scheduleRepository.save(schedule);

        // Publicar evento para notificar al profesor
        if (schedule.getTeacherId() != null) {
            ScheduleEvent event = ScheduleEvent.builder()
                    .eventType("SCHEDULE_CREATED")
                    .timestamp(LocalDateTime.now())
                    .scheduleId(saved.getId())
                    .courseId(saved.getCourse() != null ? saved.getCourse().getId() : null)
                    .teacherId(saved.getTeacherId())
                    .confirmationRequired(true)
                    .confirmationDeadline(LocalDateTime.now().plusHours(24))
                    .build();

            eventProducer.publishScheduleEvent(event);
        }

        return saved;
    }

    @Override
    @Transactional
    public Schedule update(Long id, Schedule schedule) {
        Schedule existing = findById(id);
        existing.setDayOfWeek(schedule.getDayOfWeek());
        existing.setStartTime(schedule.getStartTime());
        existing.setEndTime(schedule.getEndTime());
        existing.setTeacherId(schedule.getTeacherId());
        existing.setNotes(schedule.getNotes());
        return scheduleRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new RuntimeException("Horario no encontrado: " + id);
        }
        scheduleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void confirmTeacherSchedule(Long teacherId, boolean confirmed) {
        List<Schedule> schedules = scheduleRepository.findByTeacherIdAndConfirmedFalse(teacherId);
        schedules.forEach(s -> s.setConfirmed(confirmed));
        scheduleRepository.saveAll(schedules);
        log.info("Horarios del profesor {} confirmados: {}", teacherId, confirmed);
    }

    @Override
    @Transactional
    public void handleTeacherRejection(Long teacherId, String reason) {
        log.info("Profesor {} rechazó horario. Motivo: {}", teacherId, reason);

        List<Schedule> schedules = scheduleRepository.findByTeacherIdAndConfirmedFalse(teacherId);

        for (Schedule schedule : schedules) {
            // Publicar evento para reasignación
            ScheduleEvent event = ScheduleEvent.builder()
                    .eventType("TEACHER_UNAVAILABLE")
                    .timestamp(LocalDateTime.now())
                    .scheduleId(schedule.getId())
                    .teacherId(teacherId)
                    .rejectionReason(reason)
                    .build();

            eventProducer.publishScheduleEvent(event);
        }
    }

    @Override
    @Transactional
    public void reassignTeacher(Long scheduleId, Long originalTeacherId) {
        log.info("Reasignando horario {} del profesor {}", scheduleId, originalTeacherId);

        Schedule schedule = findById(scheduleId);

        // Aquí se debería llamar al servicio de asignación de profesores vía Kafka
        // Por ahora marcamos como pendiente de asignación
        schedule.setTeacherId(null);
        schedule.setConfirmed(false);
        schedule.setNotes("Pendiente de reasignación - Profesor original: " + originalTeacherId);
        scheduleRepository.save(schedule);

        // Publicar evento solicitando nuevo profesor
        ScheduleEvent event = ScheduleEvent.builder()
                .eventType("TEACHER_ASSIGNMENT_REQUESTED")
                .timestamp(LocalDateTime.now())
                .scheduleId(scheduleId)
                .courseId(schedule.getCourse() != null ? schedule.getCourse().getId() : null)
                .build();

        eventProducer.publishScheduleEvent(event);
    }
}

