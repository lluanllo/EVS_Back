package com.empresa.course.MicroserviceCourseApi.Controller;

import com.empresa.course.MicroserviceCourseApi.Entities.WeatherData;
import com.empresa.course.MicroserviceCourseApi.Service.Inter.IWeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final IWeatherService weatherService;

    @GetMapping("/current")
    public ResponseEntity<WeatherData> getCurrentWeather() {
        return weatherService.getLatestWeather()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(weatherService.fetchCurrentWeather()));
    }

    @PostMapping("/refresh")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'TEACHER')")
    public ResponseEntity<WeatherData> refreshWeather() {
        return ResponseEntity.ok(weatherService.fetchCurrentWeather());
    }

    @GetMapping("/last-24h")
    public ResponseEntity<List<WeatherData>> getLast24Hours() {
        return ResponseEntity.ok(weatherService.getLast24Hours());
    }

    @GetMapping("/last-week")
    public ResponseEntity<List<WeatherData>> getLastWeek() {
        return ResponseEntity.ok(weatherService.getLastWeek());
    }

    @GetMapping("/range")
    public ResponseEntity<List<WeatherData>> getWeatherRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(weatherService.getWeatherBetween(start, end));
    }

    @GetMapping("/safe-for-sailing")
    public ResponseEntity<Map<String, Object>> isSafeForSailing() {
        boolean safe = weatherService.isSafeForSailing();
        WeatherData latest = weatherService.getLatestWeather().orElse(null);

        return ResponseEntity.ok(Map.of(
                "safe", safe,
                "currentWind", latest != null ? latest.getWindSpeed() : 0,
                "windDirection", latest != null ? latest.getWindDirection() : "N/A",
                "timestamp", latest != null ? latest.getTimestamp() : LocalDateTime.now()
        ));
    }

    @GetMapping("/safe-for-course/{courseType}")
    public ResponseEntity<Map<String, Object>> isSafeForCourseType(@PathVariable String courseType) {
        boolean safe = weatherService.isSafeForCourseType(courseType);
        WeatherData latest = weatherService.getLatestWeather().orElse(null);

        return ResponseEntity.ok(Map.of(
                "courseType", courseType,
                "safe", safe,
                "currentWind", latest != null ? latest.getWindSpeed() : 0,
                "recommendation", safe ? "Condiciones aptas para " + courseType : "Condiciones no recomendadas"
        ));
    }

    @GetMapping("/prediction")
    public ResponseEntity<IWeatherService.WeatherPrediction> getPrediction() {
        return ResponseEntity.ok(weatherService.getPrediction());
    }
}

