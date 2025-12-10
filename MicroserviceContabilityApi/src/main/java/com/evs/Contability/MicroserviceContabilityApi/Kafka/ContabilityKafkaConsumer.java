package com.evs.Contability.MicroserviceContabilityApi.Kafka;

import com.evs.Contability.MicroserviceContabilityApi.DTO.WorkedHoursDTO;
import com.evs.Contability.MicroserviceContabilityApi.Service.Inter.IWorkedHoursService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContabilityKafkaConsumer {

    private final IWorkedHoursService workedHoursService;

    /**
     * Escucha eventos de clases completadas para registrar horas trabajadas automáticamente
     */
    @KafkaListener(topics = "class-completed-events", groupId = "contability-group")
    public void handleClassCompleted(Map<String, Object> event) {
        log.info("Evento de clase completada recibido: {}", event);

        try {
            Long teacherId = Long.valueOf(event.get("teacherId").toString());
            Long courseId = Long.valueOf(event.get("courseId").toString());
            String date = event.get("date").toString();
            String startTime = event.get("startTime").toString();
            String endTime = event.get("endTime").toString();

            WorkedHoursDTO dto = WorkedHoursDTO.builder()
                    .teacherId(teacherId)
                    .courseId(courseId)
                    .date(java.time.LocalDate.parse(date))
                    .startTime(java.time.LocalTime.parse(startTime))
                    .endTime(java.time.LocalTime.parse(endTime))
                    .activityType("CLASE")
                    .notes("Registrado automáticamente desde evento de clase")
                    .build();

            workedHoursService.registerHours(dto);
            log.info("Horas registradas automáticamente para profesor {} en curso {}", teacherId, courseId);

        } catch (Exception e) {
            log.error("Error al procesar evento de clase completada: {}", e.getMessage(), e);
        }
    }

    /**
     * Escucha eventos de alquiler completado para registrar en contabilidad
     */
    @KafkaListener(topics = "rental-payment-events", groupId = "contability-group")
    public void handleRentalPayment(Map<String, Object> event) {
        log.info("Evento de pago de alquiler recibido: {}", event);
        // Procesar pago de alquiler
    }

    /**
     * Escucha eventos de inscripción a regata para registrar pago
     */
    @KafkaListener(topics = "regatta-registration-events", groupId = "contability-group")
    public void handleRegattaRegistration(Map<String, Object> event) {
        log.info("Evento de inscripción a regata recibido: {}", event);
        // Procesar inscripción a regata
    }
}

