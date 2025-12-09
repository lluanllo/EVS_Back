package com.empresa.course.MicroserviceCourseApi.Kafka.Consumer;

import com.empresa.course.MicroserviceCourseApi.Kafka.Config.KafkaTopicConfig;
import com.empresa.course.MicroserviceCourseApi.Kafka.Events.ScheduleEvent;
import com.empresa.course.MicroserviceCourseApi.Service.Inter.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Consumidor de eventos Kafka para el microservicio de cursos
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseEventConsumer {

    private final ScheduleService scheduleService;

    /**
     * Escucha eventos de confirmación de horario de profesores
     */
    @KafkaListener(
        topics = KafkaTopicConfig.SCHEDULE_EVENTS_TOPIC,
        groupId = "course-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeScheduleEvent(ScheduleEvent event) {
        log.info("Evento de horario recibido: {} para profesor: {}",
            event.getEventType(), event.getTeacherId());

        try {
            switch (event.getEventType()) {
                case "CONFIRMATION_RECEIVED":
                    handleConfirmation(event);
                    break;
                case "TEACHER_UNAVAILABLE":
                    handleTeacherUnavailable(event);
                    break;
                default:
                    log.debug("Tipo de evento no manejado: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Error procesando evento de horario: {}", e.getMessage(), e);
        }
    }

    private void handleConfirmation(ScheduleEvent event) {
        if (event.getConfirmed()) {
            log.info("Profesor {} ha confirmado disponibilidad", event.getTeacherId());
            scheduleService.confirmTeacherSchedule(event.getTeacherId(), true);
        } else {
            log.info("Profesor {} ha rechazado - Motivo: {}",
                event.getTeacherId(), event.getRejectionReason());
            scheduleService.handleTeacherRejection(event.getTeacherId(), event.getRejectionReason());
        }
    }

    private void handleTeacherUnavailable(ScheduleEvent event) {
        log.info("Profesor {} no disponible, reasignando...", event.getTeacherId());
        scheduleService.reassignTeacher(event.getScheduleId(), event.getTeacherId());
    }
}

