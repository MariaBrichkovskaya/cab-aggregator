package com.modsen.driverservice.integration.kafka;

import com.modsen.driverservice.dto.request.EditDriverStatusRequest;
import com.modsen.driverservice.entity.Driver;
import com.modsen.driverservice.enums.Status;
import com.modsen.driverservice.integration.IntegrationTestStructure;
import com.modsen.driverservice.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;

import static com.modsen.driverservice.util.DriverTestUtils.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))

public class ConsumerIntegrationTest extends IntegrationTestStructure {
    @Value("${topic.name.status}")
    private String topic;

    private final DriverRepository driverRepository;

    private final KafkaTemplate<String, Object> testProducerKafkaTemplate;


    @Test
    void editDriverStatus_whenStatusMessageConsumed() {
        EditDriverStatusRequest statusRequest = EditDriverStatusRequest.builder()
                .driverId(DEFAULT_ID)
                .build();
        ProducerRecord<String, Object> record = new ProducerRecord<>(
                topic, statusRequest
        );
        testProducerKafkaTemplate.send(record);

        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(30, SECONDS)
                .untilAsserted(() -> {
                    Driver driver = driverRepository.findById(DEFAULT_ID).get();
                    assertEquals(driver.getStatus(), Status.UNAVAILABLE);
                });
    }
}
