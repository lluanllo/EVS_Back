package com.empresa.course.MicroserviceCourseApi.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Datos meteorológicos obtenidos de la estación de la escuela de vela
 */
@Data
@Builder
@Document(collection = "weather_data")
@AllArgsConstructor
@NoArgsConstructor
public class WeatherData {

    @Id
    private String id;

    private LocalDateTime timestamp;

    // Temperatura
    private Double temperature;
    private Double maxTemperature;
    private Double minTemperature;

    // Humedad
    private Double humidity;
    private Double maxHumidity;
    private Double minHumidity;

    // Viento
    private Double windSpeed;          // Velocidad actual en nudos
    private Double windGust;           // Ráfaga máxima
    private String windDirection;      // Dirección (N, NE, E, SE, S, SW, W, NW)
    private Integer windDirectionDegrees; // Dirección en grados

    // Promedios de viento
    private Double windAverage2Min;    // Promedio 2 minutos
    private Double windAverage10Min;   // Promedio 10 minutos

    // Presión atmosférica
    private Double barometer;
    private String barometerTrend;     // Rising, Falling, Steady

    // Índices
    private Double heatIndex;
    private Double dewPoint;
    private Double wetBulb;
    private Double thswIndex;
    private Double windChill;

    // Radiación solar
    private Double solarRadiation;
    private Double uvIndex;

    // Lluvia
    private Double rainRate;
    private Double rainHour;
    private Double rainDay;
    private Double rainMonth;
    private Double rainYear;

    // Evapotranspiración
    private Double evapotranspirationDay;
    private Double evapotranspirationMonth;
    private Double evapotranspirationYear;

    // Metadata
    private String source;
    private Boolean isValid;
}

