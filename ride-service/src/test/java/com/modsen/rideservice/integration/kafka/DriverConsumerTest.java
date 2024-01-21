package com.modsen.rideservice.integration.kafka;

import com.modsen.rideservice.dto.request.DriverForRideRequest;
import com.modsen.rideservice.entity.Ride;
import com.modsen.rideservice.integration.IntegrationTest;
import com.modsen.rideservice.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;

import static com.modsen.rideservice.util.TestUtils.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DriverConsumerTest extends IntegrationTest {
    @Value("${topic.name.driver}")
    private String topic;


    private final KafkaTemplate<String, DriverForRideRequest> testProducerKafkaTemplate;
    private final RideRepository rideRepository;


    @Test
    void editRideInfo_whenDriverMessageConsumed() {
        DriverForRideRequest driverMessage = DriverForRideRequest.builder()
                .driverId(DEFAULT_ID)
                .rideId(2L)
                .build();
        ProducerRecord<String, DriverForRideRequest> record = new ProducerRecord<>(
                topic, driverMessage
        );
        testProducerKafkaTemplate.send(record);

        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(30, SECONDS)
                .untilAsserted(() -> {
                    Ride ride = rideRepository.findById(2L).get();
                    assertEquals(ride.getDriverId(), driverMessage.driverId());
                });
    }
}
