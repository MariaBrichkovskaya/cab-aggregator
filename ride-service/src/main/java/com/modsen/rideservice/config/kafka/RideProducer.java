package com.modsen.rideservice.config.kafka;

import com.modsen.rideservice.dto.request.RideRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideProducer {
    @Value("${topic.name.ride}")
    private String rideTopic;
    private static final Logger LOGGER = LoggerFactory.getLogger(RideProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(RideRequest request){
        LOGGER.info(String.format("Message sent %s", request));
        kafkaTemplate.send( rideTopic,request);
    }
}
