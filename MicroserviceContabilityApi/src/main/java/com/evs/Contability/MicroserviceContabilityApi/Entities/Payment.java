package com.evs.Contability.MicroserviceContabilityApi.Entities;

import com.evs.Contability.MicroserviceContabilityApi.Entities.Enums.PaymentMethod;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Enums.PaymentStatus;
import com.evs.Contability.MicroserviceContabilityApi.Entities.Enums.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un pago en el sistema
 */
@Data
@Entity
@Builder
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDIENTE;

    // ID del estudiante que realiza el pago
    @Column(name = "student_id")
    private Long studentId;

    // ID del curso relacionado (si aplica)
    @Column(name = "course_id")
    private Long courseId;

    // ID del alquiler relacionado (si aplica)
    @Column(name = "rental_id")
    private Long rentalId;

    // ID de la regata relacionada (si aplica)
    @Column(name = "regatta_id")
    private Long regattaId;

    // Fecha del pago
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    // Fecha de confirmación
    @Column(name = "confirmed_date")
    private LocalDateTime confirmedDate;

    // ID del usuario que registró el pago
    @Column(name = "registered_by")
    private Long registeredBy;

    // Descripción o concepto del pago
    @Column(length = 500)
    private String description;

    // Número de recibo o factura
    @Column(name = "receipt_number")
    private String receiptNumber;

    // Notas adicionales
    @Column(length = 1000)
    private String notes;
}

