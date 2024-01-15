package com.modsen.rideservice.integration.kafka;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.modsen.rideservice.dto.request.EditDriverStatusRequest;
import com.modsen.rideservice.dto.request.StatusRequest;
import com.modsen.rideservice.dto.response.DriverResponse;
import com.modsen.rideservice.dto.response.PassengerResponse;
import com.modsen.rideservice.enums.RideStatus;
import com.modsen.rideservice.integration.IntegrationTest;
import com.modsen.rideservice.service.RideService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.ConsumerFactory;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.modsen.rideservice.util.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ExtendWith(WireMockExtension.class)
public class StatusProducerTest extends IntegrationTest {
    @Value("${topic.name.status}")
    private String topic;
    private final RideService rideService;
    private final ConsumerFactory<String, Object> testConsumerFactory;
    private WireMockServer driverServer;
    private WireMockServer passengerServer;

    @BeforeEach
    public void setup() {
        driverServer = new WireMockServer(9002);
        driverServer.start();

        passengerServer = new WireMockServer(9001);
        passengerServer.start();
    }

    @AfterEach
    public void teardown() {
        driverServer.stop();
        passengerServer.stop();
    }

    @Test
    public void sendStatusMessage_WhenStatusChangedToFinished() {
        DriverResponse driverResponse = getDefaultDriverResponse();
        driverServer.stubFor(get(urlPathMatching("/api/v1/drivers/" + DEFAULT_ID))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("content-type", "application/json")
                        .withBody(fromObjectToString(driverResponse)))
        );
        PassengerResponse passengerResponse = getDefaultPassengerResponse();
        passengerServer.stubFor(get(urlPathMatching("/api/v1/passengers/" + DEFAULT_ID))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("content-type", "application/json")
                        .withBody(fromObjectToString(passengerResponse)))
        );
        rideService.editStatus(DEFAULT_ID, StatusRequest.builder()
                .status(RideStatus.FINISHED.toString())
                .build());
        Consumer<String, Object> consumer = testConsumerFactory.createConsumer();
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
