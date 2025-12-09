package com.empresa.course.MicroserviceCourseApi.Controller.DTO;

import com.empresa.course.MicroserviceCourseApi.Entities.Enums.CourseType;
import com.empresa.course.MicroserviceCourseApi.Entities.Turno;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;

    @NotNull(message = "El turno es obligatorio")
    private Turno turno;

    @NotNull(message = "El tipo de curso es obligatorio")
    private CourseType courseType;

    private Integer durationMinutes;

    private Integer maxStudents;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private Set<Long> teacherIds;

    private Set<Long> studentIds;

    private Double price;
}

