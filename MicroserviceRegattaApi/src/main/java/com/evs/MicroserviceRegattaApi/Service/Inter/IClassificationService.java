package com.evs.MicroserviceRegattaApi.Service.Inter;

/**
 * Interfaz para el cálculo de clasificaciones (Single Responsibility)
 */
public interface IClassificationService {

    /**
     * Calcula las posiciones tras finalizar una manga
     */
    void calculateRacePositions(Long raceId);

    /**
     * Actualiza los puntos totales de todos los participantes de una regata
     */
    void updateTotalPoints(Long regattaId);

    /**
     * Calcula la clasificación final de la regata
     */
    void calculateFinalClassification(Long regattaId);

    /**
     * Aplica descarte si hay suficientes mangas
     */
    void applyDiscards(Long regattaId, int numberOfDiscards);
}

