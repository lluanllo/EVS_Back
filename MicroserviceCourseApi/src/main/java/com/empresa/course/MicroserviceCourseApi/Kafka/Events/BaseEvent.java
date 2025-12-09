package com.empresa.course.MicroserviceCourseApi.Kafka.Events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Evento base para todos los eventos Kafka
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseEvent implements Serializable {
    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;
    private String source;
}

