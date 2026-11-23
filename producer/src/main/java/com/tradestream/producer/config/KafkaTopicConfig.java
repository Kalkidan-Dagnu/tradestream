package com.tradestream.producer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topics.trade-events}")
    private String tradeEventsTopic;

    @Bean
    public NewTopic tradeEventsTopic() {
        return TopicBuilder.name(tradeEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
