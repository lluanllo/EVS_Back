package com.empresa.course.MicroserviceCourseApi.Kafka.Producer;

import com.empresa.course.MicroserviceCourseApi.Kafka.Config.KafkaTopicConfig;
import com.empresa.course.MicroserviceCourseApi.Kafka.Events.CourseEvent;
import com.empresa.course.MicroserviceCourseApi.Kafka.Events.NotificationEvent;
import com.empresa.course.MicroserviceCourseApi.Kafka.Events.ScheduleEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Productor de eventos Kafka para el microservicio de cursos
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publica un evento de curso
     */
    public void publishCourseEvent(CourseEvent event) {
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID().toString());
        }

        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(KafkaTopicConfig.COURSE_EVENTS_TOPIC, event.getCourseId().toString(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Evento de curso enviado: {} - Offset: {}",
                    event.getEventType(), result.getRecordMetadata().offset());
            } else {
                log.error("Error al enviar evento de curso: {}", ex.getMessage());
            }
        });
    }

    /**
     * Publica un evento de horario
     */
    public void publishScheduleEvent(ScheduleEvent event) {
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID().toString());
        }

        String key = event.getTeacherId() != null ? event.getTeacherId().toString() : UUID.randomUUID().toString();

        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(KafkaTopicConfig.SCHEDULE_EVENTS_TOPIC, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Evento de horario enviado: {} - Offset: {}",
                    event.getEventType(), result.getRecordMetadata().offset());
            } else {
                log.error("Error al enviar evento de horario: {}", ex.getMessage());
            }
        });
    }

    /**
     * Publica un evento de notificación
     */
    public void publishNotificationEvent(NotificationEvent event) {
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID().toString());
        }

        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(KafkaTopicConfig.NOTIFICATION_EVENTS_TOPIC, event.getRecipientEmail(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Evento de notificación enviado a: {} - Offset: {}",
                    event.getRecipientEmail(), result.getRecordMetadata().offset());
            } else {
                log.error("Error al enviar evento de notificación: {}", ex.getMessage());
            }
        });
    }
}

