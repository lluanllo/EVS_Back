package com.evs.Contability.MicroserviceContabilityApi.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Registro de horas trabajadas por un profesor
 */
@Data
@Entity
@Builder
@Table(name = "worked_hours")
@AllArgsConstructor
@NoArgsConstructor
public class WorkedHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID del profesor
    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    // Fecha del trabajo
    @Column(nullable = false)
    private LocalDate date;

    // Hora de inicio
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    // Hora de fin
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // Duración en minutos (calculado)
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    // ID del curso impartido
    @Column(name = "course_id")
    private Long courseId;

    // Tipo de actividad (CLASE, REGATA, ALQUILER, MANTENIMIENTO)
    @Column(name = "activity_type")
    private String activityType;

    // Si ha sido validado por el BOSS
    @Column(nullable = false)
    private Boolean validated = false;

    // ID del usuario que validó
    @Column(name = "validated_by")
    private Long validatedBy;

    // Notas del profesor
    @Column(length = 500)
    private String notes;

    // Si ha sido pagado
    @Column(nullable = false)
    private Boolean paid = false;

    // ID de la nómina donde se pagó
    @Column(name = "payroll_id")
    private Long payrollId;

    @PrePersist
    @PreUpdate
    public void calculateDuration() {
        if (startTime != null && endTime != null) {
            this.durationMinutes = (int) java.time.Duration.between(startTime, endTime).toMinutes();
        }
    }
}

