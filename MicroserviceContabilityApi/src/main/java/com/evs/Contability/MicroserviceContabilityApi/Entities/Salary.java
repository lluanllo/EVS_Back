package com.evs.Contability.MicroserviceContabilityApi.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Configuración salarial de un profesor
 */
@Data
@Entity
@Builder
@Table(name = "salaries")
@AllArgsConstructor
@NoArgsConstructor
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID del profesor
    @Column(name = "teacher_id", nullable = false, unique = true)
    private Long teacherId;

    // Tarifa por hora
    @Column(name = "hourly_rate", nullable = false, precision = 8, scale = 2)
    private BigDecimal hourlyRate;

    // Tipo de contrato (FIJO, TEMPORAL, PRACTICAS)
    @Column(name = "contract_type", nullable = false)
    private String contractType;

    // Bonus mensual (si aplica)
    @Column(name = "monthly_bonus", precision = 8, scale = 2)
    private BigDecimal monthlyBonus;

    // Plus por especialidad
    @Column(name = "speciality_bonus", precision = 8, scale = 2)
    private BigDecimal specialityBonus;

    // IRPF a aplicar (porcentaje)
    @Column(name = "irpf_percentage", precision = 5, scale = 2)
    private BigDecimal irpfPercentage;

    // Seguro Social (porcentaje)
    @Column(name = "ss_percentage", precision = 5, scale = 2)
    private BigDecimal ssPercentage;

    // Cuenta bancaria para transferencias
    @Column(name = "bank_account")
    private String bankAccount;

    // Activo o no
    @Column(nullable = false)
    private Boolean active = true;
}

