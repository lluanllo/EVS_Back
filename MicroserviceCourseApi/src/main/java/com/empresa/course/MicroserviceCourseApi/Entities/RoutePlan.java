package com.empresa.course.MicroserviceCourseApi.Entities;

import com.empresa.course.MicroserviceCourseApi.Entities.Enums.CourseType;
import com.empresa.course.MicroserviceCourseApi.Entities.Enums.WindDirection;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Plan de ruta de navegación generado para una clase
 */
@Data
@Entity
@Builder
@Table(name = "route_plans")
@AllArgsConstructor
@NoArgsConstructor
public class RoutePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Referencia al curso
    @Column(name = "course_id")
    private Long courseId;

    // Tipo de clase
    @Enumerated(EnumType.STRING)
    @Column(name = "course_type")
    private CourseType courseType;

    // Dirección del viento
    @Enumerated(EnumType.STRING)
    @Column(name = "wind_direction")
    private WindDirection windDirection;

    // Velocidad del viento en nudos
    @Column(name = "wind_speed_knots")
    private Integer windSpeedKnots;

    // Duración total de la clase en minutos
    @Column(name = "class_duration_minutes")
    private Integer classDurationMinutes;

    // Nivel del estudiante (1-4)
    @Column(name = "student_level")
    private Integer studentLevel;

    // Posición de la playa
    @Column(name = "beach_latitude")
    private Double beachLatitude;

    @Column(name = "beach_longitude")
    private Double beachLongitude;

    // Orientación de la playa en grados
    @Column(name = "beach_orientation")
    private Integer beachOrientation;

    // Tramos de la ruta
    @OneToMany(mappedBy = "routePlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("order ASC")
    private List<RouteLeg> legs = new ArrayList<>();

    // Fecha de creación
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Ruta de la imagen generada
    @Column(name = "image_path")
    private String imagePath;

    // Imagen en Base64 (para envío directo)
    @Column(name = "image_base64", columnDefinition = "TEXT")
    private String imageBase64;

    // Resumen textual del plan
    @Column(length = 2000)
    private String summary;

    // Notas de seguridad
    @Column(name = "safety_notes", length = 1000)
    private String safetyNotes;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

