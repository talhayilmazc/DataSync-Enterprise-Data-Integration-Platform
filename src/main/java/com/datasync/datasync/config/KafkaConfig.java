package com.datasync.datasync.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic syncEventsTopic() {
        return TopicBuilder.name("sync-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic syncCompletedTopic() {
        return TopicBuilder.name("sync-completed")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic syncFailedTopic() {
        return TopicBuilder.name("sync-failed")
                .partitions(3)
                .replicas(1)
                .build();
    }
}