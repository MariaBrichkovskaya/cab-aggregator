package com.modsen.rideservice.config.kafka;

import com.modsen.rideservice.dto.request.EditDriverStatusRequest;
import com.modsen.rideservice.dto.request.RideRequest;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class StatusProducerConfig {

    @Value("${topic.name.status}")
    private String statusTopic;
    @Value("${kafka.partitions.count}")
    private int PARTITIONS_COUNT;
    @Value("${kafka.replicas.count}")
    private int REPLICAS_COUNT;
    @Value("${kafka.message.status}")
    private String STATUS_MESSAGE;
    private final KafkaProperties kafkaProperties;

    @Bean
    public ProducerFactory<String, Object> statusProducerFactory() {
        Map<String, Object> properties = kafkaProperties.buildProducerProperties();
        properties.put(JsonSerializer.TYPE_MAPPINGS, STATUS_MESSAGE + EditDriverStatusRequest.class.getName());
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<String, Object> statusKafkaTemplate() {
        return new KafkaTemplate<>(statusProducerFactory());
    }


    @Bean
    public NewTopic sendStatusTopic() {
        return TopicBuilder.name(statusTopic)
                .partitions(PARTITIONS_COUNT)
                .replicas(REPLICAS_COUNT)
                .build();
    }
}
