package com.empresa.course.MicroserviceCourseApi.Entities;

import com.empresa.course.MicroserviceCourseApi.Entities.Enums.EquipmentStatus;
import com.empresa.course.MicroserviceCourseApi.Entities.Enums.EquipmentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Equipamiento disponible para alquiler
 */
@Data
@Entity
@Builder
@Table(name = "equipment")
@AllArgsConstructor
@NoArgsConstructor
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_type", nullable = false)
    private EquipmentType equipmentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentStatus status = EquipmentStatus.DISPONIBLE;

    // Número de serie o identificador único
    @Column(name = "serial_number", unique = true)
    private String serialNumber;

    // Precio por hora
    @Column(name = "price_per_hour", precision = 8, scale = 2)
    private BigDecimal pricePerHour;

    // Precio por día
    @Column(name = "price_per_day", precision = 8, scale = 2)
    private BigDecimal pricePerDay;

    // Fecha de última revisión/mantenimiento
    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    // Próxima fecha de mantenimiento
    @Column(name = "next_maintenance_date")
    private LocalDate nextMaintenanceDate;

    // Nivel mínimo requerido para alquilar (BEGINNER, INTERMEDIATE, ADVANCED)
    @Column(name = "min_skill_level")
    private String minSkillLevel;

    // Notas sobre el equipamiento
    @Column(length = 1000)
    private String notes;

    // Si es apto para socios
    @Column(name = "member_only")
    private Boolean memberOnly = false;
}

