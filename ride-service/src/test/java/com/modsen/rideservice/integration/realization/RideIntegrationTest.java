package com.modsen.rideservice.integration.realization;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.modsen.rideservice.dto.request.StatusRequest;
import com.modsen.rideservice.dto.response.DriverResponse;
import com.modsen.rideservice.dto.response.ExceptionResponse;
import com.modsen.rideservice.dto.response.MessageResponse;
import com.modsen.rideservice.dto.response.PassengerResponse;
import com.modsen.rideservice.dto.response.RideResponse;
import com.modsen.rideservice.entity.Ride;
import com.modsen.rideservice.enums.RideStatus;
import com.modsen.rideservice.integration.IntegrationTest;
import com.modsen.rideservice.repository.RideRepository;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.modsen.rideservice.util.Messages.*;
import static com.modsen.rideservice.util.TestUtils.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(WireMockExtension.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RideIntegrationTest extends IntegrationTest {
    private final RideRepository rideRepository;
    private final ModelMapper modelMapper;
    @LocalServerPort
    private int port;
    private WireMockServer driverServer;
    private WireMockServer passengerServer;

    private final PassengerResponse passengerResponse = getDefaultPassengerResponse();
    private final DriverResponse driverResponse = getDefaultDriverResponse();

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
    void findById_shouldReturnNotFoundResponse_whenRideNotExist() {
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(String.format(NOT_FOUND_WITH_ID_MESSAGE, NOT_FOUND_ID))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, NOT_FOUND_ID)
                .when()
                .get(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findById_shouldReturnDriverResponse_whenDriverExists() {
        driverServer.stubFor(get(urlPathMatching("/api/v1/drivers/" + DEFAULT_ID))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("content-type", "application/json")
                        .withBody(fromObjectToString(driverResponse)))
        );

        passengerServer.stubFor(get(urlPathMatching("/api/v1/passengers/" + DEFAULT_ID))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("content-type", "application/json")
                        .withBody(fromObjectToString(passengerResponse)))
        );
        Ride ride = rideRepository.findById(DEFAULT_ID).get();
        RideResponse expected = modelMapper.map(ride, RideResponse.class);
        expected.setDriverResponse(driverResponse);
        expected.setPassengerResponse(passengerResponse);
        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .when()
                .get(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RideResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getRidesByPassengerId_whenValidParamsPassed() {
        Page<Ride> ridePage = rideRepository.findAllByPassengerId(DEFAULT_ID,
                PageRequest.of(VALID_PAGE - 1, VALID_SIZE, Sort.by(VALID_ORDER_BY))
        );
        List<RideResponse> expected = ridePage.stream()
                .map(ride -> modelMapper.map(ride, RideResponse.class))
                .toList();

        var actual = given()
                .port(port)
                .pathParam(PASSENGER_PARAM_NAME, DEFAULT_ID)
                .params(Map.of(
                        PAGE_PARAM_NAME, VALID_PAGE,
                        SIZE_PARAM_NAME, VALID_SIZE,
                        ORDER_BY_PARAM_NAME, VALID_ORDER_BY)
                )
                .when()
                .get(GET_BY_PASSENGER_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().body().jsonPath().getList("rides", RideResponse.class);

        assertThat(actual).isEqualTo(expected);
        assertThat(rideRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    void getRidesByPassengerId_shouldReturnBadRequestResponse_whenInvalidPagePassed() {
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(INVALID_PAGE_MESSAGE)
                .build();

        var actual = given()
                .port(port)
                .pathParam(PASSENGER_PARAM_NAME, DEFAULT_ID)
                .params(Map.of(
                        PAGE_PARAM_NAME, INVALID_PAGE,
                        SIZE_PARAM_NAME, VALID_SIZE,
                        ORDER_BY_PARAM_NAME, VALID_ORDER_BY)
                )
                .when()
                .get(GET_BY_PASSENGER_ID_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getRidesByPassengerId_shouldReturnBadRequestResponse_whenInvalidSizePassed() {
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(INVALID_PAGE_MESSAGE)
                .build();

        var actual = given()
                .port(port)
                .pathParam(PASSENGER_PARAM_NAME, DEFAULT_ID)
                .params(Map.of(
                        PAGE_PARAM_NAME, VALID_PAGE,
                        SIZE_PARAM_NAME, INVALID_SIZE,
                        ORDER_BY_PARAM_NAME, VALID_ORDER_BY
                ))
                .when()
                .get(GET_BY_PASSENGER_ID_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getRidesByPassengerId_shouldReturnBadRequestResponse_whenInvalidOrderByParamPassed() {
        String errorMessage = getInvalidSortingMessage();
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(errorMessage)
                .build();

        var actual = given()
                .port(port)
                .pathParam(PASSENGER_PARAM_NAME, DEFAULT_ID)
                .params(Map.of(
                        PAGE_PARAM_NAME, VALID_PAGE,
                        SIZE_PARAM_NAME, VALID_SIZE,
                        ORDER_BY_PARAM_NAME, INVALID_ORDER_BY
                ))
                .when()
                .get(GET_BY_PASSENGER_ID_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAll_whenValidParamsPassed() {
        Page<Ride> ridePage = rideRepository.findAll(
                PageRequest.of(VALID_PAGE - 1, VALID_SIZE, Sort.by(VALID_ORDER_BY))
        );
        List<RideResponse> expected = ridePage.stream()
                .map(ride -> modelMapper.map(ride, RideResponse.class))
                .toList();

        var actual = given()
                .port(port)
                .params(Map.of(
                        PAGE_PARAM_NAME, VALID_PAGE,
                        SIZE_PARAM_NAME, VALID_SIZE,
                        ORDER_BY_PARAM_NAME, VALID_ORDER_BY)
                )
                .when()
                .get(DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().body().jsonPath().getList("rides", RideResponse.class);

        assertThat(actual).isEqualTo(expected);
        assertThat(rideRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    void findAll_shouldReturnBadRequestResponse_whenInvalidPagePassed() {
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(INVALID_PAGE_MESSAGE)
                .build();

        var actual = given()
                .port(port)
                .params(Map.of(
                        PAGE_PARAM_NAME, INVALID_PAGE,
                        SIZE_PARAM_NAME, VALID_SIZE,
                        ORDER_BY_PARAM_NAME, VALID_ORDER_BY)
                )
                .when()
                .get(DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }


    @Test
    void findAll_shouldReturnBadRequestResponse_whenInvalidSizePassed() {
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(INVALID_PAGE_MESSAGE)
                .build();

        var actual = given()
                .port(port)
                .params(Map.of(
                        PAGE_PARAM_NAME, VALID_PAGE,
                        SIZE_PARAM_NAME, INVALID_SIZE,
                        ORDER_BY_PARAM_NAME, VALID_ORDER_BY
                ))
                .when()
                .get(DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAll_shouldReturnBadRequestResponse_whenInvalidOrderByParamPassed() {
        String errorMessage = getInvalidSortingMessage();
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(errorMessage)
                .build();

        var actual = given()
                .port(port)
                .params(Map.of(
                        PAGE_PARAM_NAME, VALID_PAGE,
                        SIZE_PARAM_NAME, VALID_SIZE,
                        ORDER_BY_PARAM_NAME, INVALID_ORDER_BY
                ))
                .when()
                .get(DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteById_shouldReturnNotFoundResponse_whenDriverNotExist() {
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(String.format(NOT_FOUND_WITH_ID_MESSAGE, NOT_FOUND_ID))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, NOT_FOUND_ID)
                .when()
                .delete(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteById_shouldReturnMessageResponse_whenDriverExists() {
        StatusRequest request = StatusRequest.builder()
                .status(RideStatus.FINISHED.name())
                .build();
        MessageResponse expected = MessageResponse.builder()
                .message(String.format(DELETE_MESSAGE, DEFAULT_ID))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .body(request)
                .when()
                .delete(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(MessageResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editStatusById_shouldReturnNotFoundResponse_whenRideNotExist() {
        StatusRequest request = StatusRequest.builder()
                .status(RideStatus.FINISHED.name())
                .build();
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(String.format(NOT_FOUND_WITH_ID_MESSAGE, NOT_FOUND_ID))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, NOT_FOUND_ID)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(STATUS_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editStatusById_shouldReturnAlreadyFinishedResponse_whenRideNotExist() {
        StatusRequest request = StatusRequest.builder()
                .status(RideStatus.FINISHED.name())
                .build();
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.CONFLICT)
                .message(ALREADY_FINISHED_MESSAGE)
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, 3L)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(STATUS_PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editStatusById_shouldReturnDriverEmptyResponse_whenRideNotExist() {
        StatusRequest request = StatusRequest.builder()
                .status(RideStatus.FINISHED.name())
                .build();
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.CONFLICT)
                .message(EMPTY_DRIVER_MESSAGE)
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, 2L)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(STATUS_PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void editStatusById_shouldReturnRideResponse() {
        StatusRequest request = StatusRequest.builder()
                .status(RideStatus.FINISHED.name())
                .build();
        RideResponse expected = modelMapper.map(rideRepository.findById(DEFAULT_ID), RideResponse.class);
        driverServer.stubFor(get(urlPathMatching("/api/v1/drivers/" + DEFAULT_ID))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("content-type", "application/json")
                        .withBody(fromObjectToString(driverResponse)))
        );

        passengerServer.stubFor(get(urlPathMatching("/api/v1/passengers/" + DEFAULT_ID))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("content-type", "application/json")
                        .withBody(fromObjectToString(passengerResponse)))
        );
        expected.setRideStatus(RideStatus.FINISHED);
        expected.setPassengerResponse(passengerResponse);
        expected.setDriverResponse(driverResponse);

        var actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .body(request)
                .when()
                .put(STATUS_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RideResponse.class);

        assertThat(actual).isEqualTo(expected);
    }


}
