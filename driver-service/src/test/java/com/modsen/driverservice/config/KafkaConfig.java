package com.modsen.driverservice.config;

import com.modsen.driverservice.dto.request.DriverForRideRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@TestConfiguration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaTemplate<String, Object> testProducerKafkaTemplate() {
        return new KafkaTemplate<>(testProducerFactory());
    }

    @Bean
    public ProducerFactory<String, Object> testProducerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaProducerConfigs());
    }

    @Bean
    public Map<String, Object> kafkaProducerConfigs() {
        return Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );
    }

    @Bean
    public Map<String, Object> kafkaConsumerConfigs() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                ConsumerConfig.GROUP_ID_CONFIG, "driver-creation-group",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
        );
    }

    @Bean
    public ConsumerFactory<String, DriverForRideRequest> testConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(kafkaConsumerConfigs());
    }
}
