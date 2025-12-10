package com.evs.MicroserviceRegattaApi.Entities;

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

    @Column(name = "skipper_id", nullable = false)
    private Long skipperId;

    @Column(name = "skipper_name")
    private String skipperName;

    @ElementCollection
    @CollectionTable(name = "participant_crew", joinColumns = @JoinColumn(name = "participant_id"))
    @Column(name = "crew_member_id")
    private List<Long> crewIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "participant_crew_names", joinColumns = @JoinColumn(name = "participant_id"))
    @Column(name = "crew_name")
    private List<String> crewNames = new ArrayList<>();

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(nullable = false)
    private String status = "INSCRITO";

    @Column(name = "fee_paid")
    private Boolean feePaid = false;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "total_points")
    private Integer totalPoints = 0;

    @Column(name = "final_position")
    private Integer finalPosition;

    @Column(length = 500)
    private String notes;
}

