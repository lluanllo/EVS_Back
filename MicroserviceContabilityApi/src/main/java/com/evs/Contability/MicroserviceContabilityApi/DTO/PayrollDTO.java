package com.evs.Contability.MicroserviceContabilityApi.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayrollDTO {

    private Long teacherId;
    private Integer month;
    private Integer year;
    private BigDecimal totalHours;
    private BigDecimal grossSalary;
    private BigDecimal irpfDeduction;
    private BigDecimal ssDeduction;
    private BigDecimal bonuses;
    private BigDecimal netSalary;
    private String notes;
}

