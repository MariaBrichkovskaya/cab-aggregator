package com.modsen.driverservice.config.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.driverservice.dto.request.RideRequest;
import com.modsen.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RideConsumer {
    private final DriverService driverService;
    private final DriverProducer driverProducer;

    @KafkaListener(topics = "${topic.name.ride}", groupId = "${spring.kafka.consumer.group-id.ride}")
    public void consumeMessage(String message) {
        log.info("message consumed {}", message);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            RideRequest createRideRequest = objectMapper.readValue(message, RideRequest.class);
            driverProducer.sendMessage(driverService.findDriverForRide(createRideRequest));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
