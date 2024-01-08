package com.modsen.rideservice.kafka;

import com.modsen.rideservice.dto.request.RideRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideProducer {
    @Value("${topic.name.ride}")
    private String rideTopic;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(RideRequest request) {
        log.info(String.format("Message sent %s", request));
        kafkaTemplate.send(rideTopic, request);
    }
}
