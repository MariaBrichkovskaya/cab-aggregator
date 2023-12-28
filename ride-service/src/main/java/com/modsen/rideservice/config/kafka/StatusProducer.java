package com.modsen.rideservice.config.kafka;

import com.modsen.rideservice.dto.request.EditDriverStatusRequest;
import com.modsen.rideservice.dto.request.RideRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatusProducer {
    @Value("${topic.name.status}")
    private String statusTopic;
    private static final Logger LOGGER = LoggerFactory.getLogger(RideProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(EditDriverStatusRequest request){
        LOGGER.info(String.format("Message sent %s", request));
        kafkaTemplate.send( statusTopic,request);
    }
}
