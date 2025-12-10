package com.empresa.course.MicroserviceCourseApi.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Participante inscrito en una regata
 */
@Data
@Entity
@Builder
@Table(name = "regatta_participants")
@AllArgsConstructor
@NoArgsConstructor
public class RegattaParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regatta_id", nullable = false)
    private Regatta regatta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boat_id", nullable = false)
    private Boat boat;

    // ID del patrón (skipper)
    @Column(name = "skipper_id", nullable = false)
    private Long skipperId;

    // Nombre del patrón
    @Column(name = "skipper_name")
    private String skipperName;

    // IDs de la tripulación
    @ElementCollection
    @CollectionTable(name = "participant_crew", joinColumns = @JoinColumn(name = "participant_id"))
    @Column(name = "crew_member_id")
    private List<Long> crewIds = new ArrayList<>();

    // Nombres de la tripulación
    @ElementCollection
    @CollectionTable(name = "participant_crew_names", joinColumns = @JoinColumn(name = "participant_id"))
    @Column(name = "crew_name")
    private List<String> crewNames = new ArrayList<>();

    // Fecha de inscripción
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    // Estado: INSCRITO, CONFIRMADO, RETIRADO, DESCALIFICADO
    @Column(nullable = false)
    private String status = "INSCRITO";

    // Si ha pagado la inscripción
    @Column(name = "fee_paid")
    private Boolean feePaid = false;

    // ID del pago
    @Column(name = "payment_id")
    private Long paymentId;

    // Puntos totales acumulados
    @Column(name = "total_points")
    private Integer totalPoints = 0;

    // Posición final en la clasificación general
    @Column(name = "final_position")
    private Integer finalPosition;

    // Notas
    @Column(length = 500)
    private String notes;
}

