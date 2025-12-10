package com.evs.Contability.MicroserviceContabilityApi.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CashRegisterDTO {

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate date;

    @NotNull(message = "El efectivo real contado es obligatorio")
    @PositiveOrZero(message = "El efectivo no puede ser negativo")
    private BigDecimal actualCash;

    @PositiveOrZero
    private BigDecimal totalTransfers;

    @PositiveOrZero
    private BigDecimal totalCard;

    @PositiveOrZero
    private BigDecimal totalBizum;

    private String discrepancyNotes;
}

