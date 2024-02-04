package com.modsen.rideservice.integration.kafka;

import com.modsen.rideservice.dto.request.EditDriverStatusRequest;
import com.modsen.rideservice.dto.request.StatusRequest;
import com.modsen.rideservice.enums.RideStatus;
import com.modsen.rideservice.integration.IntegrationTest;
import com.modsen.rideservice.service.RideService;
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

public class StatusProducerTest extends IntegrationTest {
    @Value("${topic.name.status}")
    private String topic;
    @Autowired
    private RideService rideService;
    @Autowired
    private ConsumerFactory<String, Object> testStatusConsumerFactory;

    @Test
    public void sendStatusMessage_WhenStatusChangedToFinished() {
        rideService.editStatus(DEFAULT_ID, StatusRequest.builder()
                .status(RideStatus.FINISHED.toString())
                .build());
        Consumer<String, Object> consumer = testStatusConsumerFactory.createConsumer();
        consumer.subscribe(Collections.singleton(topic));
        ConsumerRecords<String, Object> records = consumer.poll(Duration.ofMillis(10000L));
        for (ConsumerRecord<String, Object> record : records) {
            ObjectMapper objectMapper = new ObjectMapper();
            EditDriverStatusRequest request = objectMapper.convertValue(record.value(), EditDriverStatusRequest.class);
            assertEquals(EditDriverStatusRequest.builder()
                    .driverId(DEFAULT_ID)
                    .build(), request);
        }
        consumer.close();
    }

}
