package com.modsen.rideservice.config.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.rideservice.dto.request.DriverForRideRequest;
import com.modsen.rideservice.service.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DriverConsumer {
    private final RideService rideService;
    @KafkaListener(topics = "${topic.name.driver}",groupId = "${spring.kafka.consumer.group-id.driver}")
    public void consumeMessage(String message) {
        log.info("message consumed {}", message);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            DriverForRideRequest driver = objectMapper.readValue(message, DriverForRideRequest.class);
            rideService.setDriver(driver);
            // Далее можно использовать объект driver
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
