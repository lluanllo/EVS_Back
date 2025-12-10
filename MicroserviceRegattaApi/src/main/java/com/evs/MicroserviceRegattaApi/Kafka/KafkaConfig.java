package com.evs.MicroserviceRegattaApi.Kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic regattaEventsTopic() {
        return TopicBuilder.name("regatta-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic regattaRegistrationEventsTopic() {
        return TopicBuilder.name("regatta-registration-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic raceEventsTopic() {
        return TopicBuilder.name("race-events")
                .partitions(3)
                .replicas(1)
                .build();
    }
}

