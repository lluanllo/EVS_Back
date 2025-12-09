package com.empresa.course.MicroserviceCourseApi.Kafka.Events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Evento de curso creado/actualizado
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseEvent implements Serializable {
    private String eventId;
    private String eventType; // CREATED, UPDATED, DELETED, TEACHER_ASSIGNED, STUDENT_ENROLLED
    private LocalDateTime timestamp;

    private Long courseId;
    private String courseName;
    private String courseType;
    private Long teacherId;
    private Long studentId;
    private String status;
}

