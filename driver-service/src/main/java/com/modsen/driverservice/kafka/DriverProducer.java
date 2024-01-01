package com.modsen.driverservice.kafka;

import com.modsen.driverservice.dto.request.DriverForRideRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverProducer {
    @Value("${topic.name.driver}")
    private String rideTopic;
    private static final Logger LOGGER = LoggerFactory.getLogger(DriverProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(DriverForRideRequest request) {
        LOGGER.info(String.format("Message sent %s", request));
        kafkaTemplate.send(rideTopic, request);
    }
}
