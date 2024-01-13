package com.modsen.driverservice.integration.kafka;

import com.modsen.driverservice.config.KafkaConfig;
import com.modsen.driverservice.dto.request.EditDriverStatusRequest;
import com.modsen.driverservice.entity.Driver;
import com.modsen.driverservice.enums.Status;
import com.modsen.driverservice.integration.IntegrationTestStructure;
import com.modsen.driverservice.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static com.modsen.driverservice.util.DriverTestUtils.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Import(KafkaConfig.class)
public class ConsumerIntegrationTest extends IntegrationTestStructure {
    @Value("${topic.name.status}")
    private String topic;

    @Container
    private static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));
    private final DriverRepository driverRepository;

    private final KafkaTemplate<String, Object> testProducerKafkaTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @BeforeAll
    public static void setUp() {
        kafka.start();
    }

    @AfterAll
    public static void tearDown() {
        kafka.stop();
    }


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
