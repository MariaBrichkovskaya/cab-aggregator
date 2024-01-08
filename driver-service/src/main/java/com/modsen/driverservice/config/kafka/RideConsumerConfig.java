package com.modsen.driverservice.config.kafka;

import com.modsen.driverservice.dto.request.RideRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RideConsumerConfig {
    private final KafkaProperties kafkaProperties;
    @Value("${kafka.message.ride}")
    private String RIDE_MESSAGE;

    @Bean
    public ConsumerFactory<String, RideRequest> consumerFactory() {
        Map<String, Object> properties = kafkaProperties.buildConsumerProperties();
        properties.put(JsonDeserializer.TYPE_MAPPINGS, RIDE_MESSAGE + RideRequest.class.getName());
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RideRequest> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RideRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
