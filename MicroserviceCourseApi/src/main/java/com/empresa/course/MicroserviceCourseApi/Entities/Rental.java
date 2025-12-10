package com.empresa.course.MicroserviceCourseApi.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Registro de alquiler de equipamiento
 */
@Data
@Entity
@Builder
@Table(name = "rentals")
@AllArgsConstructor
@NoArgsConstructor
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Equipamiento alquilado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    // ID del cliente/estudiante
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    // Fecha y hora de inicio
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    // Fecha y hora de fin prevista
    @Column(name = "expected_end_time", nullable = false)
    private LocalDateTime expectedEndTime;

    // Fecha y hora de devolución real
    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    // Estado del alquiler
    @Column(nullable = false)
    private String status = "ACTIVO"; // ACTIVO, COMPLETADO, CANCELADO, RETRASADO

    // Precio total
    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    // Si ya se ha pagado
    @Column(name = "paid")
    private Boolean paid = false;

    // ID del pago asociado
    @Column(name = "payment_id")
    private Long paymentId;

    // Verificación de aptitud
    @Column(name = "skill_verified")
    private Boolean skillVerified = false;

    // Usuario que verificó la aptitud
    @Column(name = "verified_by")
    private Long verifiedBy;

    // Notas
    @Column(length = 1000)
    private String notes;

    // Usuario que registró el alquiler
    @Column(name = "registered_by")
    private Long registeredBy;

    // Condiciones del equipo al devolver
    @Column(name = "return_condition")
    private String returnCondition;

    // Daños reportados
    @Column(name = "damage_reported", length = 1000)
    private String damageReported;
}

