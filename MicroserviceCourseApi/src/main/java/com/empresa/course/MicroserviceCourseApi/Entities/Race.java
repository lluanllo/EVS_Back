package com.empresa.course.MicroserviceCourseApi.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Manga (carrera) dentro de una regata
 */
@Data
@Entity
@Builder
@Table(name = "races")
@AllArgsConstructor
@NoArgsConstructor
public class Race {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regatta_id", nullable = false)
    private Regatta regatta;

    @Column(name = "race_number", nullable = false)
    private Integer raceNumber;

    // Hora de salida
    @Column(name = "start_time")
    private LocalDateTime startTime;

    // Estado: PLANIFICADA, SEÑAL_PREPARATORIA, EN_CURSO, FINALIZADA, ABANDONADA
    @Column(nullable = false)
    private String status = "PLANIFICADA";

    // Dirección del viento al inicio
    @Column(name = "wind_direction")
    private Integer windDirection;

    // Velocidad del viento al inicio
    @Column(name = "wind_speed")
    private Double windSpeed;

    // Número de vueltas al recorrido
    @Column(name = "number_of_laps")
    private Integer numberOfLaps = 1;

    // Tipo de recorrido: BARLOVENTO_SOTAVENTO, TRIANGULO, TRAPECIO
    @Column(name = "course_type")
    private String courseType;

    // Notas del comité
    @Column(length = 1000)
    private String notes;
}

