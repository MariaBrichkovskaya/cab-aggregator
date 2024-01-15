package com.modsen.rideservice.integration.kafka;

import com.modsen.rideservice.dto.request.RideRequest;
import com.modsen.rideservice.dto.response.PassengerResponse;
import com.modsen.rideservice.integration.IntegrationTest;
import com.modsen.rideservice.service.RideService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.ConsumerFactory;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.modsen.rideservice.util.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@AutoConfigureWireMock(port = 9001)
public class RideProducerTest extends IntegrationTest {
    @Value("${topic.name.ride}")
    private String topic;
    private final RideService rideService;
    private final ConsumerFactory<String, Object> testConsumerFactory;

    @Test
    public void sendRideMessage_WhenRideCreated() {
        PassengerResponse passengerResponse = getDefaultPassengerResponse();
        stubFor(get(urlPathMatching("/api/v1/passengers/1"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("content-type", "application/json")
                        .withBody(fromObjectToString(passengerResponse)))
        );
        rideService.add(getRideRequestWhitCash());
        Consumer<String, Object> consumer = testConsumerFactory.createConsumer();
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
