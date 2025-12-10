package com.evs.Contability.MicroserviceContabilityApi.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Registro de cierre de caja diario
 */
@Data
@Entity
@Builder
@Table(name = "cash_registers")
@AllArgsConstructor
@NoArgsConstructor
public class CashRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fecha del cierre
    @Column(nullable = false)
    private LocalDate date;

    // Efectivo esperado según registros
    @Column(name = "expected_cash", nullable = false, precision = 10, scale = 2)
    private BigDecimal expectedCash;

    // Efectivo real contado
    @Column(name = "actual_cash", nullable = false, precision = 10, scale = 2)
    private BigDecimal actualCash;

    // Diferencia (puede ser positiva o negativa)
    @Column(precision = 10, scale = 2)
    private BigDecimal difference;

    // Total en transferencias del día
    @Column(name = "total_transfers", precision = 10, scale = 2)
    private BigDecimal totalTransfers;

    // Total en tarjeta del día
    @Column(name = "total_card", precision = 10, scale = 2)
    private BigDecimal totalCard;

    // Total en Bizum del día
    @Column(name = "total_bizum", precision = 10, scale = 2)
    private BigDecimal totalBizum;

    // Total general del día
    @Column(name = "total_day", precision = 10, scale = 2)
    private BigDecimal totalDay;

    // Si hay descuadre
    @Column(name = "has_discrepancy")
    private Boolean hasDiscrepancy = false;

    // Notas sobre el descuadre
    @Column(name = "discrepancy_notes", length = 2000)
    private String discrepancyNotes;

    // ID del usuario que cerró la caja
    @Column(name = "closed_by")
    private Long closedBy;

    // Fecha y hora del cierre
    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    // Estado (ABIERTO, CERRADO, REVISADO)
    @Column(nullable = false)
    private String status = "ABIERTO";

    @PrePersist
    @PreUpdate
    public void calculateDifference() {
        if (expectedCash != null && actualCash != null) {
            this.difference = actualCash.subtract(expectedCash);
            this.hasDiscrepancy = this.difference.compareTo(BigDecimal.ZERO) != 0;
        }

        // Calcular total del día
        BigDecimal total = actualCash != null ? actualCash : BigDecimal.ZERO;
        total = total.add(totalTransfers != null ? totalTransfers : BigDecimal.ZERO);
        total = total.add(totalCard != null ? totalCard : BigDecimal.ZERO);
        total = total.add(totalBizum != null ? totalBizum : BigDecimal.ZERO);
        this.totalDay = total;
    }
}

