package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "schedules")
public class Schedule {

    @Id
    private String id;

    private Long teacherId;

    private Long courseId;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer hours;

    private String status; // CONFIRMED, PENDING, CANCELLED

    private String notes;
}
