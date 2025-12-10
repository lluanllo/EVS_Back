package com.evs.Contability.MicroserviceContabilityApi.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Nómina mensual de un profesor
 */
@Data
@Entity
@Builder
@Table(name = "payrolls")
@AllArgsConstructor
@NoArgsConstructor
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID del profesor
    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    // Mes y año
    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    // Total horas trabajadas
    @Column(name = "total_hours", precision = 8, scale = 2)
    private BigDecimal totalHours;

    // Salario bruto
    @Column(name = "gross_salary", precision = 10, scale = 2)
    private BigDecimal grossSalary;

    // Deducciones IRPF
    @Column(name = "irpf_deduction", precision = 10, scale = 2)
    private BigDecimal irpfDeduction;

    // Deducciones Seguridad Social
    @Column(name = "ss_deduction", precision = 10, scale = 2)
    private BigDecimal ssDeduction;

    // Bonus aplicados
    @Column(precision = 10, scale = 2)
    private BigDecimal bonuses;

    // Salario neto
    @Column(name = "net_salary", precision = 10, scale = 2)
    private BigDecimal netSalary;

    // Estado (PENDIENTE, APROBADO, PAGADO)
    @Column(nullable = false)
    private String status = "PENDIENTE";

    // Fecha de generación
    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    // Fecha de pago
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    // ID del usuario que aprobó
    @Column(name = "approved_by")
    private Long approvedBy;

    // IDs de las horas trabajadas incluidas
    @ElementCollection
    @CollectionTable(name = "payroll_worked_hours", joinColumns = @JoinColumn(name = "payroll_id"))
    @Column(name = "worked_hours_id")
    private List<Long> workedHoursIds = new ArrayList<>();

    // Notas
    @Column(length = 1000)
    private String notes;
}

