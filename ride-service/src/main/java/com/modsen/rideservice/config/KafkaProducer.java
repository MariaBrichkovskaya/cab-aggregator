package com.modsen.rideservice.config;

import com.modsen.rideservice.dto.request.CreateRideRequest;
import com.modsen.rideservice.dto.request.KafkaRideRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, KafkaRideRequest> kafkaTemplate;


    public void sendMessage(KafkaRideRequest request){
        Message<KafkaRideRequest> message= MessageBuilder.withPayload(request)
                        .setHeader(KafkaHeaders.TOPIC,"creation")
                                .build();
        LOGGER.info(String.format("Message sent %s", request));
        kafkaTemplate.send( message);
    }
}
