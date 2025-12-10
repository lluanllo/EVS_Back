package com.evs.Contability.MicroserviceContabilityApi.Kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic paymentEventsTopic() {
        return TopicBuilder.name("payment-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic workedHoursEventsTopic() {
        return TopicBuilder.name("worked-hours-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic payrollEventsTopic() {
        return TopicBuilder.name("payroll-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic cashRegisterEventsTopic() {
        return TopicBuilder.name("cash-register-events")
                .partitions(3)
                .replicas(1)
                .build();
    }
}

