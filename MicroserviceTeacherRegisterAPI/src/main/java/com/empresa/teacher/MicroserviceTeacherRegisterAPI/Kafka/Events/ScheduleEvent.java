package com.empresa.teacher.MicroserviceTeacherRegisterAPI.Kafka.Events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleEvent implements Serializable {
    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;

    private Long scheduleId;
    private Long courseId;
    private Long teacherId;
    private String teacherEmail;
    private String teacherName;

    private List<ScheduleInfo> schedules;

    private Boolean confirmationRequired;
    private LocalDateTime confirmationDeadline;

    private Boolean confirmed;
    private String rejectionReason;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScheduleInfo implements Serializable {
        private String dayOfWeek;
        private String startTime;
        private String endTime;
        private String courseName;
        private String courseType;
    }
}

