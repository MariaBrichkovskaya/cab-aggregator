package com.modsen.driverservice.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.driverservice.dto.request.CreateRideRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "creation", groupId = "my-group-id")
    public void consume(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            CreateRideRequest createRideRequest = objectMapper.readValue(message, CreateRideRequest.class);
            System.err.println(createRideRequest);
            // Далее можно использовать объект createRideRequest
        } catch ( JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
