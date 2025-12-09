package com.empresa.course.MicroserviceCourseApi.Kafka.Events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Evento de horario/planificación
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleEvent implements Serializable {
    private String eventId;
    private String eventType; // SCHEDULE_CREATED, SCHEDULE_UPDATED, CONFIRMATION_REQUESTED, CONFIRMATION_RECEIVED
    private LocalDateTime timestamp;

    private Long scheduleId;
    private Long courseId;
    private Long teacherId;
    private String teacherEmail;
    private String teacherName;

    // Lista de horarios asignados
    private List<ScheduleInfo> schedules;

    // Si es una solicitud de confirmación
    private Boolean confirmationRequired;
    private LocalDateTime confirmationDeadline;

    // Respuesta de confirmación
    private Boolean confirmed;
    private String rejectionReason;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScheduleInfo implements Serializable {
        private String dayOfWeek;
        private String startTime;
        private String endTime;
        private String courseName;
        private String courseType;
    }
}

