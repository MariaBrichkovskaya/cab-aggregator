package com.modsen.driverservice.integration.kafka;

import com.modsen.driverservice.config.KafkaConfig;
import com.modsen.driverservice.dto.request.DriverForRideRequest;
import com.modsen.driverservice.integration.IntegrationTestStructure;
import com.modsen.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Import(KafkaConfig.class)
public class DriverProducerIntegrationTest extends IntegrationTestStructure {
    private final DriverService driverService;

    @Value("${topic.name.driver}")
    private String topic;
    @Container
    private static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));
    private final ConsumerFactory<String, DriverForRideRequest> testConsumerFactory;

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
