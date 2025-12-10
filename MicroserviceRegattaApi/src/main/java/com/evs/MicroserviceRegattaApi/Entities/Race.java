package com.evs.MicroserviceRegattaApi.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(name = "start_time")
    private LocalDateTime startTime;

    // PLANIFICADA, SEÑAL_PREPARATORIA, EN_CURSO, FINALIZADA, ABANDONADA
    @Column(nullable = false)
    private String status = "PLANIFICADA";

    @Column(name = "wind_direction")
    private Integer windDirection;

    @Column(name = "wind_speed")
    private Double windSpeed;

    @Column(name = "number_of_laps")
    private Integer numberOfLaps = 1;

    @Column(name = "course_type")
    private String courseType;

    @Column(length = 1000)
    private String notes;
}

