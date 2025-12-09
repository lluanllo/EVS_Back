package com.empresa.course.MicroserviceCourseApi.Kafka.Config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Configuración de topics de Kafka
 */
@Configuration
public class KafkaTopicConfig {

    public static final String COURSE_EVENTS_TOPIC = "course-events";
    public static final String TEACHER_EVENTS_TOPIC = "teacher-events";
    public static final String STUDENT_EVENTS_TOPIC = "student-events";
    public static final String SCHEDULE_EVENTS_TOPIC = "schedule-events";
    public static final String NOTIFICATION_EVENTS_TOPIC = "notification-events";

    @Bean
    public NewTopic courseEventsTopic() {
        return TopicBuilder.name(COURSE_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic teacherEventsTopic() {
        return TopicBuilder.name(TEACHER_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic studentEventsTopic() {
        return TopicBuilder.name(STUDENT_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic scheduleEventsTopic() {
        return TopicBuilder.name(SCHEDULE_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic notificationEventsTopic() {
        return TopicBuilder.name(NOTIFICATION_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}

