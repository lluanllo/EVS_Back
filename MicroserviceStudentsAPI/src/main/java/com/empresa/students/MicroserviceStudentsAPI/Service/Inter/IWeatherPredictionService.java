package com.empresa.students.MicroserviceStudentsAPI.Service.Inter;

import java.util.Map;

/**
 * Interfaz para consulta de predicción meteorológica (Single Responsibility)
 */
public interface IWeatherPredictionService {

    /**
     * Obtiene la predicción del viento actual
     */
    Map<String, Object> getWeatherPrediction();

    /**
     * Obtiene recomendación de actividad según condiciones
     */
    String getActivityRecommendation();

    /**
     * Verifica si las condiciones son aptas para principiantes
     */
    boolean isSuitableForBeginners();

    /**
     * Verifica si las condiciones son aptas para avanzados
     */
    boolean isSuitableForAdvanced();
}

