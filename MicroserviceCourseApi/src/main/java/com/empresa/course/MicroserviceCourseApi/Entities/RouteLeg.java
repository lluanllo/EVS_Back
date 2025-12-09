package com.empresa.course.MicroserviceCourseApi.Entities;

import com.empresa.course.MicroserviceCourseApi.Entities.Enums.ManeuverType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa un tramo (leg) de una ruta de navegación
 */
@Data
@Entity
@Builder
@Table(name = "route_legs")
@AllArgsConstructor
@NoArgsConstructor
public class RouteLeg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_plan_id")
    private RoutePlan routePlan;

    // Orden del tramo en la ruta
    @Column(name = "leg_order")
    private Integer order;

    // Rumbo en grados
    private Integer heading;

    // Duración estimada en minutos
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    // Distancia estimada en metros
    @Column(name = "distance_meters")
    private Integer distanceMeters;

    // Tipo de maniobra
    @Enumerated(EnumType.STRING)
    @Column(name = "maneuver_type")
    private ManeuverType maneuverType;

    // Punto de inicio (latitud)
    @Column(name = "start_lat")
    private Double startLatitude;

    // Punto de inicio (longitud)
    @Column(name = "start_lng")
    private Double startLongitude;

    // Punto final (latitud)
    @Column(name = "end_lat")
    private Double endLatitude;

    // Punto final (longitud)
    @Column(name = "end_lng")
    private Double endLongitude;

    // Descripción del tramo
    private String description;
}

