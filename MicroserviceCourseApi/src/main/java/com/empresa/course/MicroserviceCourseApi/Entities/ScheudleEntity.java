package com.empresa.course.MicroserviceCourseApi.Entities;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode
public class ScheudleEntity {

    @Id
    private Long id;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
}
