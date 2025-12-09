package com.empresa.course.MicroserviceCourseApi.Kafka.Events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Evento de notificación
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent implements Serializable {
    private String eventId;
    private String eventType; // EMAIL, SMS, PUSH
    private LocalDateTime timestamp;

    private String recipientEmail;
    private String recipientPhone;
    private String recipientName;

    private String subject;
    private String message;
    private String templateName;

    // Datos adicionales para la plantilla
    private String templateData;

    // Prioridad: HIGH, MEDIUM, LOW
    private String priority;

    // Si incluye adjuntos
    private Boolean hasAttachment;
    private String attachmentBase64;
    private String attachmentName;
}

