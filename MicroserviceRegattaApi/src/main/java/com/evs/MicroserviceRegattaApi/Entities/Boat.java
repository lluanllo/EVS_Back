package com.evs.MicroserviceRegattaApi.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Barco inscrito en regatas con su rating para handicap
 */
@Data
@Entity
@Builder
@Table(name = "boats")
@AllArgsConstructor
@NoArgsConstructor
public class Boat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "sail_number", nullable = false, unique = true)
    private String sailNumber;

    @Column(name = "boat_class", nullable = false)
    private String boatClass;

    @Column(precision = 6, scale = 3)
    private BigDecimal rating;

    @Column(name = "auto_rating")
    private Boolean autoRating = true;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(precision = 5, scale = 2)
    private BigDecimal length;

    @Column(precision = 5, scale = 2)
    private BigDecimal beam;

    @Column(name = "sail_area", precision = 6, scale = 2)
    private BigDecimal sailArea;

    @Column(name = "build_year")
    private Integer buildYear;

    @Column(length = 500)
    private String notes;

    @Column(name = "photo_id")
    private String photoId;

    @PrePersist
    @PreUpdate
    public void calculateAutoRating() {
        if (autoRating && boatClass != null) {
            this.rating = getDefaultRatingForClass(boatClass);
        }
    }

    private BigDecimal getDefaultRatingForClass(String boatClass) {
        return switch (boatClass.toUpperCase()) {
            case "HOBIE_16" -> new BigDecimal("0.950");
            case "CATAMARAN_F18" -> new BigDecimal("1.100");
            case "NACRA_17" -> new BigDecimal("1.050");
            case "NACRA_15" -> new BigDecimal("0.980");
            case "DART_16" -> new BigDecimal("0.900");
            case "TORNADO" -> new BigDecimal("1.150");
            default -> new BigDecimal("1.000");
        };
    }
}

