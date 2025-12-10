package com.evs.Contability.MicroserviceContabilityApi.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalaryDTO {

    @NotNull(message = "El ID del profesor es obligatorio")
    private Long teacherId;

    @NotNull(message = "La tarifa por hora es obligatoria")
    @Positive(message = "La tarifa debe ser positiva")
    private BigDecimal hourlyRate;

    @NotNull(message = "El tipo de contrato es obligatorio")
    private String contractType;

    private BigDecimal monthlyBonus;
    private BigDecimal specialityBonus;
    private BigDecimal irpfPercentage;
    private BigDecimal ssPercentage;
    private String bankAccount;
}

