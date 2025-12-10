package com.evs.Contability.MicroserviceContabilityApi.Entities.Enums;

/**
 * Tipos de pago que se pueden registrar en el sistema
 */
public enum PaymentType {
    CLASE,          // Pago por clase individual
    CURSO,          // Pago por curso completo
    ALQUILER,       // Pago por alquiler de material
    SUMMERCAMP,     // Pago por campamento de verano
    REGATA,         // Inscripción a regata
    SOCIO,          // Cuota de socio
    OTRO            // Otros pagos
}

