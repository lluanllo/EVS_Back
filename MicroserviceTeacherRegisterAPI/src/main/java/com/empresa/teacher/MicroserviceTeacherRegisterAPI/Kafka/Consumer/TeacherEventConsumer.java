package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Kafka.Consumer;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Kafka.Config.KafkaTopicConfig;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Kafka.Events.ScheduleEvent;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service.NotificationService;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherEventConsumer {

    private final TeacherService teacherService;
    private final NotificationService notificationService;

    @KafkaListener(
        topics = KafkaTopicConfig.SCHEDULE_EVENTS_TOPIC,
        groupId = "teacher-group"
    )
    public void consumeScheduleEvent(ScheduleEvent event) {
        log.info("Evento de horario recibido: {} para profesor: {}",
            event.getEventType(), event.getTeacherId());

        try {
            switch (event.getEventType()) {
                case "SCHEDULE_CREATED":
                    handleScheduleCreated(event);
                    break;
                case "CONFIRMATION_REQUESTED":
                    handleConfirmationRequested(event);
                    break;
                case "TEACHER_ASSIGNMENT_REQUESTED":
                    handleTeacherAssignmentRequested(event);
                    break;
                default:
                    log.debug("Tipo de evento no manejado: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Error procesando evento de horario: {}", e.getMessage(), e);
        }
    }

    private void handleScheduleCreated(ScheduleEvent event) {
        if (event.getTeacherId() == null) return;

        // Construir detalles del horario
        String scheduleDetails = "";
        if (event.getSchedules() != null && !event.getSchedules().isEmpty()) {
            scheduleDetails = event.getSchedules().stream()
                    .map(s -> String.format("- %s: %s - %s (%s)",
                            s.getDayOfWeek(), s.getStartTime(), s.getEndTime(), s.getCourseName()))
                    .collect(Collectors.joining("\n"));
        }

        // Enviar notificación al profesor
        notificationService.sendScheduleNotification(event.getTeacherId(), scheduleDetails);
    }

    private void handleConfirmationRequested(ScheduleEvent event) {
        if (event.getTeacherId() == null) return;
        notificationService.sendConfirmationRequest(event.getTeacherId());
    }

    private void handleTeacherAssignmentRequested(ScheduleEvent event) {
        log.info("Solicitud de asignación de profesor para horario: {}", event.getScheduleId());
        // Aquí se podría implementar lógica adicional de asignación
    }
}

