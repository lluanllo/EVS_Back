package com.evs.Contability.MicroserviceContabilityApi.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CashDiscrepancyReportDTO {

    private LocalDate date;
    private BigDecimal expectedCash;
    private BigDecimal actualCash;
    private BigDecimal difference;

    // Desglose de pagos esperados en efectivo
    private List<PaymentDetailDTO> expectedPayments;

    // Totales por método
    private BigDecimal totalEfectivo;
    private BigDecimal totalTransferencia;
    private BigDecimal totalTarjeta;
    private BigDecimal totalBizum;

    // Análisis del descuadre
    private String analysisNotes;
    private List<String> possibleReasons;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentDetailDTO {
        private Long paymentId;
        private String description;
        private BigDecimal amount;
        private String paymentMethod;
        private String status;
        private String studentName;
    }
}

