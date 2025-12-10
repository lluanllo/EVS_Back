package com.evs.Contability.MicroserviceContabilityApi.Entities.Enums;

/**
 * Estados de un pago
 */
public enum PaymentStatus {
    PENDIENTE,      // Pago pendiente de confirmar
    CONFIRMADO,     // Pago confirmado
    CANCELADO,      // Pago cancelado
    REEMBOLSADO     // Pago reembolsado
}

