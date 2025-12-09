package com.empresa.course.MicroserviceCourseApi.Controller.DTO;

import com.empresa.course.MicroserviceCourseApi.Entities.Enums.CourseType;
import com.empresa.course.MicroserviceCourseApi.Entities.Turno;
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
public class CourseResponse {
    private Long id;
    private String name;
    private String description;
    private String status;
    private Turno turno;
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
    private Boolean active;
    private Integer currentStudents;
}

