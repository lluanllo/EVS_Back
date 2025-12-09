package com.empresa.course.MicroserviceCourseApi.Controller.DTO;

import com.empresa.course.MicroserviceCourseApi.Entities.Enums.CourseType;
import com.empresa.course.MicroserviceCourseApi.Entities.Enums.ManeuverType;
import com.empresa.course.MicroserviceCourseApi.Entities.Enums.WindDirection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoutePlanResponse {
    private Long id;
    private Long courseId;
    private CourseType courseType;
    private WindDirection windDirection;
    private Integer windSpeedKnots;
    private Integer classDurationMinutes;
    private Integer studentLevel;
    private List<RouteLegResponse> legs;
    private LocalDateTime createdAt;
    private String summary;
    private String safetyNotes;
    private String imageBase64;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RouteLegResponse {
        private Integer order;
        private ManeuverType maneuverType;
        private Integer heading;
        private Integer durationMinutes;
        private Integer distanceMeters;
        private String description;
    }
}

