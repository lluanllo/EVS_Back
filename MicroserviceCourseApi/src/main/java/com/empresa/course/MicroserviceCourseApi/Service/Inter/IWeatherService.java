package com.empresa.course.MicroserviceCourseApi.Service.Inter;

import com.empresa.course.MicroserviceCourseApi.Entities.WeatherData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IWeatherService {

    /**
     * Obtiene los datos meteorológicos actuales haciendo scraping de la web
     */
    WeatherData fetchCurrentWeather();

    /**
     * Obtiene los últimos datos almacenados
     */
    Optional<WeatherData> getLatestWeather();

    /**
     * Obtiene el historial de las últimas 24 horas
     */
    List<WeatherData> getLast24Hours();

    /**
     * Obtiene el historial de los últimos 7 días
     */
    List<WeatherData> getLastWeek();

    /**
     * Obtiene datos entre dos fechas
     */
    List<WeatherData> getWeatherBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Determina si las condiciones son aptas para navegación
     */
    boolean isSafeForSailing();

    /**
     * Determina si las condiciones son aptas para un tipo de curso específico
     */
    boolean isSafeForCourseType(String courseType);

    /**
     * Obtiene una predicción básica basada en tendencias
     */
    WeatherPrediction getPrediction();

    /**
     * Clase interna para predicción
     */
    record WeatherPrediction(
            String windTrend,
            String recommendation,
            boolean suitableForBeginners,
            boolean suitableForAdvanced,
            String bestActivityType
    ) {}
}

