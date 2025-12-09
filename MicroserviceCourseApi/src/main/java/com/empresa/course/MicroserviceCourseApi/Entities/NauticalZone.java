package com.empresa.course.MicroserviceCourseApi.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuración del canal náutico y zonas de navegación
 */
@Data
@Entity
@Builder
@Table(name = "nautical_zones")
@AllArgsConstructor
@NoArgsConstructor
public class NauticalZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    // Tipo de zona: SAFE, RESTRICTED, FORBIDDEN
    @Column(name = "zone_type")
    private String zoneType;

    // Nivel mínimo requerido para navegar en esta zona
    @Column(name = "min_level")
    private Integer minLevel = 1;

    // Coordenadas del polígono que define la zona (JSON)
    @Column(name = "polygon_coords", columnDefinition = "TEXT")
    private String polygonCoords;

    // Centro de la zona
    @Column(name = "center_lat")
    private Double centerLatitude;

    @Column(name = "center_lng")
    private Double centerLongitude;

    // Radio en metros (para zonas circulares)
    @Column(name = "radius_meters")
    private Integer radiusMeters;

    // Si la zona está activa
    private Boolean active = true;

    // Notas adicionales
    private String notes;
}

