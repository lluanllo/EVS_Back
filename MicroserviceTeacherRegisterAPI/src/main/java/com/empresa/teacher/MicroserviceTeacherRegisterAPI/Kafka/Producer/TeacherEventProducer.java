package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Kafka.Producer;

import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Kafka.Config.KafkaTopicConfig;
import com.empresa.teacher.MicroserviceTeacherRegisterAPI.Kafka.Events.ScheduleEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publica confirmación de horario de un profesor
     */
    public void publishScheduleConfirmation(Long teacherId, boolean confirmed, String reason) {
        ScheduleEvent event = ScheduleEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("CONFIRMATION_RECEIVED")
                .timestamp(LocalDateTime.now())
                .teacherId(teacherId)
                .confirmed(confirmed)
                .rejectionReason(reason)
                .build();

        kafkaTemplate.send(KafkaTopicConfig.SCHEDULE_EVENTS_TOPIC, teacherId.toString(), event);
        log.info("Confirmación de horario publicada para profesor: {} - Confirmado: {}", teacherId, confirmed);
    }

    /**
     * Publica evento de profesor no disponible
     */
    public void publishTeacherUnavailable(Long scheduleId, Long teacherId) {
        ScheduleEvent event = ScheduleEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("TEACHER_UNAVAILABLE")
                .timestamp(LocalDateTime.now())
                .scheduleId(scheduleId)
                .teacherId(teacherId)
                .build();

        kafkaTemplate.send(KafkaTopicConfig.SCHEDULE_EVENTS_TOPIC, teacherId.toString(), event);
        log.info("Evento de profesor no disponible publicado: {}", teacherId);
    }
}

