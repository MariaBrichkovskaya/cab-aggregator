package com.modsen.rideservice.config.kafka;

import com.modsen.rideservice.dto.request.EditDriverStatusRequest;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
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
    private static final int PARTITIONS_COUNT = 1;
    private static final int REPLICAS_COUNT = 1;
    private static final String STATUS_MESSAGE = "statusMessage:";

    @Bean
    public ProducerFactory<String, Object> statusProducerFactory() {
        Map<String, Object> configProps = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class,
                JsonSerializer.TYPE_MAPPINGS, STATUS_MESSAGE + EditDriverStatusRequest.class.getName());
        return new DefaultKafkaProducerFactory<>(configProps);
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
