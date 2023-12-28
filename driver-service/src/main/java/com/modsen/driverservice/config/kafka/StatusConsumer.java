package com.modsen.driverservice.config.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.driverservice.dto.request.EditStatusRequest;
import com.modsen.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class StatusConsumer {
    private final DriverService driverService;

    @KafkaListener(topics = "${topic.name.status}", groupId = "${spring.kafka.consumer.group-id.status}")
    public void consumeMessage(String message) {
        log.info("message consumed {}", message);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            EditStatusRequest editStatusRequest = objectMapper.readValue(message, EditStatusRequest.class);
            driverService.changeStatus(editStatusRequest.getDriverId());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
