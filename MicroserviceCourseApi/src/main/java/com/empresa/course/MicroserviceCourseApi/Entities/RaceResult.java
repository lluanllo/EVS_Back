package com.empresa.course.MicroserviceCourseApi.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;

/**
 * Resultado de un participante en una manga
 */
@Data
@Entity
@Builder
@Table(name = "race_results")
@AllArgsConstructor
@NoArgsConstructor
public class RaceResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id", nullable = false)
    private Race race;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private RegattaParticipant participant;

    // Tiempo de llegada real
    @Column(name = "finish_time")
    private LocalTime finishTime;

    // Tiempo en segundos desde la salida
    @Column(name = "elapsed_seconds")
    private Long elapsedSeconds;

    // Tiempo corregido (después de aplicar rating)
    @Column(name = "corrected_seconds")
    private Long correctedSeconds;

    // Posición en la manga
    @Column(name = "position")
    private Integer position;

    // Puntos obtenidos (normalmente igual a posición, con penalizaciones)
    @Column(name = "points")
    private Integer points;

    // Estado: FINISH, DNF, DNS, DSQ, OCS, BFD, UFD, RET
    @Column(nullable = false)
    private String status = "FINISH";

    // Penalizaciones adicionales
    @Column(name = "penalty_points")
    private Integer penaltyPoints = 0;

    // Notas (protestas, penalizaciones, etc.)
    @Column(length = 500)
    private String notes;

    /**
     * Calcula el tiempo corregido basado en el rating del barco
     */
    public void calculateCorrectedTime(BigDecimal rating) {
        if (elapsedSeconds != null && rating != null && rating.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal corrected = new BigDecimal(elapsedSeconds).divide(rating, 0, java.math.RoundingMode.HALF_UP);
            this.correctedSeconds = corrected.longValue();
        }
    }

    /**
     * Asigna puntos según el estado
     */
    public void assignPoints(int totalParticipants) {
        this.points = switch (status.toUpperCase()) {
            case "FINISH" -> position != null ? position : totalParticipants + 1;
            case "DNF", "RET" -> totalParticipants + 1;
            case "DNS" -> totalParticipants + 1;
            case "DSQ", "OCS", "BFD", "UFD" -> totalParticipants + 1;
            default -> totalParticipants + 1;
        };
        this.points += penaltyPoints;
    }
}

