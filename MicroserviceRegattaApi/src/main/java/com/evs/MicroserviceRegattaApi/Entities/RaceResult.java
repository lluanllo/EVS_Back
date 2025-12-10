package com.evs.MicroserviceRegattaApi.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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

    @Column(name = "finish_time")
    private LocalTime finishTime;

    @Column(name = "elapsed_seconds")
    private Long elapsedSeconds;

    @Column(name = "corrected_seconds")
    private Long correctedSeconds;

    @Column(name = "position")
    private Integer position;

    @Column(name = "points")
    private Integer points;

    // FINISH, DNF, DNS, DSQ, OCS, BFD, UFD, RET
    @Column(nullable = false)
    private String status = "FINISH";

    @Column(name = "penalty_points")
    private Integer penaltyPoints = 0;

    @Column(length = 500)
    private String notes;

    public void calculateCorrectedTime(BigDecimal rating) {
        if (elapsedSeconds != null && rating != null && rating.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal corrected = new BigDecimal(elapsedSeconds).divide(rating, 0, java.math.RoundingMode.HALF_UP);
            this.correctedSeconds = corrected.longValue();
        }
    }

    public void assignPoints(int totalParticipants) {
        this.points = switch (status.toUpperCase()) {
            case "FINISH" -> position != null ? position : totalParticipants + 1;
            case "DNF", "RET", "DNS", "DSQ", "OCS", "BFD", "UFD" -> totalParticipants + 1;
            default -> totalParticipants + 1;
        };
        this.points += penaltyPoints;
    }
}

