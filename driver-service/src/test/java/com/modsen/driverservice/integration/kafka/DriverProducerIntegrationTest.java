package com.modsen.driverservice.integration.kafka;

import com.modsen.driverservice.dto.request.DriverForRideRequest;
import com.modsen.driverservice.integration.IntegrationTestStructure;
import com.modsen.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DriverProducerIntegrationTest extends IntegrationTestStructure {
    private final DriverService driverService;

    @Value("${topic.name.driver}")
    private String topic;
    private final ConsumerFactory<String, DriverForRideRequest> testConsumerFactory;


    @Test
    public void sendDriverMessage_WhenStatusChangeToAvailable() {
        driverService.changeStatus(2L);
        Consumer<String, DriverForRideRequest> consumer = testConsumerFactory.createConsumer();
        consumer.subscribe(Collections.singleton(topic));

        ConsumerRecords<String, DriverForRideRequest> records = consumer.poll(Duration.ofMillis(10000L));

        for (ConsumerRecord<String, DriverForRideRequest> record : records) {
            DriverForRideRequest receivedMessage = record.value();
            assertEquals((DriverForRideRequest.builder().driverId(2L).rideId(0).build()), receivedMessage);
        }
        consumer.close();

    }


}
