package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Representa un horario de clase dentro de un curso
 */
@Data
@Entity
@Builder
@Table(name = "schedules")
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id")
    private Long courseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    // Profesor asignado a este horario específico
    @Column(name = "teacher_id")
    private Long teacherId;

    // Si la clase está confirmada
    private Boolean confirmed = false;

    // Notas adicionales
    private String notes;
}

