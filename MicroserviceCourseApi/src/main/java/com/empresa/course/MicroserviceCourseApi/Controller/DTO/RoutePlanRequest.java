package com.empresa.course.MicroserviceCourseApi.Controller.DTO;

import com.empresa.course.MicroserviceCourseApi.Entities.Enums.CourseType;
import com.empresa.course.MicroserviceCourseApi.Entities.Enums.WindDirection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoutePlanRequest {

    private Long courseId;

    @NotNull(message = "El tipo de clase es obligatorio")
    private CourseType courseType;

    @NotNull(message = "La dirección del viento es obligatoria")
    private WindDirection windDirection;

    @Min(value = 1, message = "La velocidad del viento debe ser al menos 1 nudo")
    @Max(value = 50, message = "La velocidad del viento no puede superar 50 nudos")
    private Integer windSpeedKnots;

    @Min(value = 30, message = "La duración mínima es 30 minutos")
    @Max(value = 180, message = "La duración máxima es 180 minutos")
    private Integer classDurationMinutes;

    @Min(value = 1, message = "El nivel mínimo es 1")
    @Max(value = 4, message = "El nivel máximo es 4")
    private Integer studentLevel;
}

