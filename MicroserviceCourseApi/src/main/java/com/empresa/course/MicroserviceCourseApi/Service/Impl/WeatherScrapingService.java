package com.empresa.course.MicroserviceCourseApi.Service.Impl;

import com.empresa.course.MicroserviceCourseApi.Entities.WeatherData;
import com.empresa.course.MicroserviceCourseApi.Repository.WeatherDataRepository;
import com.empresa.course.MicroserviceCourseApi.Service.Inter.IWeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherScrapingService implements IWeatherService {

    private static final String METEO_URL = "https://www.escuela-vela.com/meteo/";
    private final WeatherDataRepository weatherDataRepository;

    @Override
    @Scheduled(fixedRate = 300000) // Cada 5 minutos
    public WeatherData fetchCurrentWeather() {
        try {
            log.info("Iniciando scraping de datos meteorológicos desde {}", METEO_URL);

            Document doc = Jsoup.connect(METEO_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

            WeatherData weatherData = parseWeatherData(doc);
            weatherData.setTimestamp(LocalDateTime.now());
            weatherData.setSource(METEO_URL);
            weatherData.setIsValid(true);

            WeatherData saved = weatherDataRepository.save(weatherData);
            log.info("Datos meteorológicos guardados: viento {}kts dirección {}",
                    saved.getWindSpeed(), saved.getWindDirection());

            return saved;

        } catch (Exception e) {
            log.error("Error al hacer scraping de datos meteorológicos: {}", e.getMessage());
            return createDefaultWeatherData();
        }
    }

    private WeatherData parseWeatherData(Document doc) {
        WeatherData.WeatherDataBuilder builder = WeatherData.builder();

        try {
            // Buscar la tabla de datos meteorológicos
            Elements rows = doc.select("table tr");

            for (Element row : rows) {
                Elements cells = row.select("td");
                if (cells.size() >= 2) {
                    String label = cells.get(0).text().toLowerCase().trim();
                    String value = cells.get(1).text().trim();

                    parseRow(builder, label, value, cells);
                }
            }

            // También intentar parsear la sección de viento específica
            parseWindSection(doc, builder);

        } catch (Exception e) {
            log.warn("Error al parsear algunos datos: {}", e.getMessage());
        }

        return builder.build();
    }

    private void parseRow(WeatherData.WeatherDataBuilder builder, String label, String value, Elements cells) {
        try {
            // Temperatura
            if (label.contains("temperatura")) {
                builder.temperature(parseDouble(value));
                if (cells.size() > 2) {
                    builder.maxTemperature(parseDouble(cells.get(2).text()));
                }
                if (cells.size() > 3) {
                    builder.minTemperature(parseDouble(cells.get(3).text()));
                }
            }
            // Humedad
            else if (label.contains("humedad")) {
                builder.humidity(parseDouble(value));
            }
            // Velocidad del viento
            else if (label.contains("velocidad") && label.contains("viento")) {
                builder.windSpeed(parseDouble(value));
            }
            // Dirección del viento
            else if (label.contains("dirección") && label.contains("viento")) {
                builder.windDirection(parseWindDirection(value));
                builder.windDirectionDegrees(parseWindDegrees(value));
            }
            // Barómetro
            else if (label.contains("barómetro") || label.contains("barometro")) {
                builder.barometer(parseDouble(value));
            }
            // Tendencia barométrica
            else if (label.contains("tendencia")) {
                builder.barometerTrend(value);
            }
            // Radiación solar
            else if (label.contains("radiación solar") || label.contains("radiacion solar")) {
                builder.solarRadiation(parseDouble(value));
            }
            // UV
            else if (label.contains("uv")) {
                builder.uvIndex(parseDouble(value));
            }
            // Punto de rocío
            else if (label.contains("rocío") || label.contains("rocio")) {
                builder.dewPoint(parseDouble(value));
            }
            // Viento frío
            else if (label.contains("viento frío") || label.contains("viento frio")) {
                builder.windChill(parseDouble(value));
            }
            // Índice de calor
            else if (label.contains("índice calorífico") || label.contains("indice calorifico")) {
                builder.heatIndex(parseDouble(value));
            }
            // Lluvia
            else if (label.contains("lluvia")) {
                if (label.contains("tasa")) {
                    builder.rainRate(parseDouble(value));
                }
            }
            // Promedio de viento 2 minutos
            else if (label.contains("promedio") && label.contains("2")) {
                builder.windAverage2Min(parseDouble(value));
            }
            // Promedio de viento 10 minutos
            else if (label.contains("promedio") && label.contains("10")) {
                builder.windAverage10Min(parseDouble(value));
            }
            // Ráfaga
            else if (label.contains("ráfaga") || label.contains("rafaga")) {
                builder.windGust(parseDouble(value));
            }

        } catch (Exception e) {
            log.debug("No se pudo parsear campo {}: {}", label, e.getMessage());
        }
    }

    private void parseWindSection(Document doc, WeatherData.WeatherDataBuilder builder) {
        try {
            // Buscar sección específica de viento
            Element windSection = doc.selectFirst("div:contains(Viento)");
            if (windSection != null) {
                String text = windSection.text();

                // Extraer velocidad de viento en knots
                if (text.contains("knots")) {
                    String[] parts = text.split("\\s+");
                    for (int i = 0; i < parts.length; i++) {
                        if (parts[i].equals("knots") && i > 0) {
                            try {
                                builder.windSpeed(Double.parseDouble(parts[i-1].replace(",", ".")));
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Error parseando sección de viento: {}", e.getMessage());
        }
    }

    private Double parseDouble(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            // Limpiar el string de unidades y caracteres especiales
            String cleaned = value.replaceAll("[^0-9.,\\-]", "")
                    .replace(",", ".")
                    .trim();
            if (cleaned.isEmpty()) return null;
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String parseWindDirection(String value) {
        if (value == null) return "N";
        String upper = value.toUpperCase().trim();

        // Si contiene grados, extraer la dirección cardinal
        if (upper.contains("°")) {
            int degrees = parseWindDegrees(value);
            return degreesToCardinal(degrees);
        }

        // Si ya es una dirección cardinal
        if (upper.matches("^[NSEW]{1,3}$")) {
            return upper;
        }

        return "N";
    }

    private Integer parseWindDegrees(String value) {
        if (value == null) return 0;
        try {
            String cleaned = value.replaceAll("[^0-9]", "");
            if (!cleaned.isEmpty()) {
                return Integer.parseInt(cleaned);
            }
        } catch (NumberFormatException ignored) {}
        return 0;
    }

    private String degreesToCardinal(int degrees) {
        String[] directions = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
                               "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
        int index = (int) Math.round(((double) degrees % 360) / 22.5);
        return directions[index % 16];
    }

    private WeatherData createDefaultWeatherData() {
        return WeatherData.builder()
                .timestamp(LocalDateTime.now())
                .source(METEO_URL)
                .isValid(false)
                .temperature(20.0)
                .windSpeed(10.0)
                .windDirection("SW")
                .build();
    }

    @Override
    public Optional<WeatherData> getLatestWeather() {
        return weatherDataRepository.findTopByOrderByTimestampDesc();
    }

    @Override
    public List<WeatherData> getLast24Hours() {
        return weatherDataRepository.findTop24ByOrderByTimestampDesc();
    }

    @Override
    public List<WeatherData> getLastWeek() {
        return weatherDataRepository.findTop168ByOrderByTimestampDesc();
    }

    @Override
    public List<WeatherData> getWeatherBetween(LocalDateTime start, LocalDateTime end) {
        return weatherDataRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);
    }

    @Override
    public boolean isSafeForSailing() {
        Optional<WeatherData> latest = getLatestWeather();
        if (latest.isEmpty()) return false;

        WeatherData weather = latest.get();
        Double windSpeed = weather.getWindSpeed();

        // Condiciones seguras: viento entre 5 y 25 nudos
        return windSpeed != null && windSpeed >= 5 && windSpeed <= 25;
    }

    @Override
    public boolean isSafeForCourseType(String courseType) {
        Optional<WeatherData> latest = getLatestWeather();
        if (latest.isEmpty()) return false;

        WeatherData weather = latest.get();
        Double windSpeed = weather.getWindSpeed() != null ? weather.getWindSpeed() : 0;

        return switch (courseType.toUpperCase()) {
            case "WINDSURF" -> windSpeed >= 8 && windSpeed <= 30;
            case "CATAMARAN" -> windSpeed >= 6 && windSpeed <= 25;
            case "MINICATA" -> windSpeed >= 4 && windSpeed <= 18;
            case "SUMMERCAMP" -> windSpeed >= 4 && windSpeed <= 15;
            case "INICIACION" -> windSpeed >= 4 && windSpeed <= 12;
            default -> windSpeed >= 5 && windSpeed <= 20;
        };
    }

    @Override
    public WeatherPrediction getPrediction() {
        List<WeatherData> recentData = getLast24Hours();

        if (recentData.isEmpty()) {
            return new WeatherPrediction("UNKNOWN", "Sin datos disponibles", false, false, "NINGUNA");
        }

        // Analizar tendencia del viento
        double avgWindRecent = recentData.stream()
                .limit(6) // Últimas 30 min
                .filter(w -> w.getWindSpeed() != null)
                .mapToDouble(WeatherData::getWindSpeed)
                .average()
                .orElse(10);

        double avgWindOlder = recentData.stream()
                .skip(6)
                .filter(w -> w.getWindSpeed() != null)
                .mapToDouble(WeatherData::getWindSpeed)
                .average()
                .orElse(10);

        String trend = avgWindRecent > avgWindOlder + 2 ? "INCREASING" :
                       avgWindRecent < avgWindOlder - 2 ? "DECREASING" : "STABLE";

        boolean suitableForBeginners = avgWindRecent >= 4 && avgWindRecent <= 12;
        boolean suitableForAdvanced = avgWindRecent >= 10 && avgWindRecent <= 30;

        String bestActivity = avgWindRecent < 8 ? "MINICATA" :
                              avgWindRecent < 15 ? "CATAMARAN" :
                              avgWindRecent < 25 ? "WINDSURF" : "NINGUNA";

        String recommendation = generateRecommendation(avgWindRecent, trend);

        return new WeatherPrediction(trend, recommendation, suitableForBeginners, suitableForAdvanced, bestActivity);
    }

    private String generateRecommendation(double windSpeed, String trend) {
        StringBuilder sb = new StringBuilder();

        if (windSpeed < 5) {
            sb.append("Viento muy flojo. Condiciones no ideales para navegación a vela. ");
        } else if (windSpeed <= 12) {
            sb.append("Viento ideal para principiantes y clases de iniciación. ");
        } else if (windSpeed <= 20) {
            sb.append("Viento moderado. Buenas condiciones para navegantes experimentados. ");
        } else if (windSpeed <= 25) {
            sb.append("Viento fuerte. Solo para navegantes avanzados. ");
        } else {
            sb.append("Viento muy fuerte. No recomendado para clases. ");
        }

        if ("INCREASING".equals(trend)) {
            sb.append("El viento está aumentando, estar atentos.");
        } else if ("DECREASING".equals(trend)) {
            sb.append("El viento está bajando.");
        }

        return sb.toString().trim();
    }
}

