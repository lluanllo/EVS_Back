package com.evs.MicroserviceRegattaApi.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Regata de catamaranes
 */
@Data
@Entity
@Builder
@Table(name = "regattas")
@AllArgsConstructor
@NoArgsConstructor
public class Regatta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "registration_deadline")
    private LocalDate registrationDeadline;

    private String location;

    // Estado: PLANIFICADA, INSCRIPCIONES_ABIERTAS, INSCRIPCIONES_CERRADAS, EN_CURSO, FINALIZADA, CANCELADA
    @Column(nullable = false)
    private String status = "PLANIFICADA";

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "registration_fee")
    private Double registrationFee;

    @Column(name = "planned_races")
    private Integer plannedRaces = 3;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(length = 2000)
    private String notes;

    @ElementCollection
    @CollectionTable(name = "regatta_staff", joinColumns = @JoinColumn(name = "regatta_id"))
    @Column(name = "teacher_id")
    private List<Long> staffIds = new ArrayList<>();
}

