package com.modsen.rideservice.config.kafka;

import com.modsen.rideservice.dto.request.DriverForRideRequest;
import com.modsen.rideservice.dto.request.EditDriverStatusRequest;
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
public class DriverConsumerConfig {
    private final KafkaProperties kafkaProperties;
    @Value("${kafka.message.driver}")
    private String DRIVER_MESSAGE ;

    @Bean
    public ConsumerFactory<String, DriverForRideRequest> consumerFactory() {
        Map<String, Object> properties = kafkaProperties.buildConsumerProperties();
        properties.put(JsonDeserializer.TYPE_MAPPINGS, DRIVER_MESSAGE + DriverForRideRequest.class.getName());
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DriverForRideRequest> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DriverForRideRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
