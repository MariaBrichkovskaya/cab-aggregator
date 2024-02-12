package com.modsen.rideservice.integration.kafka;

import com.modsen.rideservice.dto.request.RideRequest;
import com.modsen.rideservice.integration.IntegrationTest;
import com.modsen.rideservice.service.RideService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Collections;

import static com.modsen.rideservice.util.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RideProducerTest extends IntegrationTest {
    @Value("${topic.name.ride}")
    private String topic;
    private final RideService rideService;
    private final ConsumerFactory<String, Object> testRideConsumerFactory;

    @Test
    public void sendRideMessage_WhenRideCreated() {
        rideService.add(getRideRequestWhitCash());
        Consumer<String, Object> consumer = testRideConsumerFactory.createConsumer();
        consumer.subscribe(Collections.singleton(topic));
        ConsumerRecords<String, Object> records = consumer.poll(Duration.ofMillis(10000L));
        for (ConsumerRecord<String, Object> record : records) {
            ObjectMapper objectMapper = new ObjectMapper();
            RideRequest request = objectMapper.convertValue(record.value(), RideRequest.class);
            assertEquals(RideRequest.builder()
                    .id(DEFAULT_ID)
                    .build(), request);
        }
        consumer.close();

    }
}
