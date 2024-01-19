package com.modsen.passengerservice.integration.controller;

import com.modsen.passengerservice.dto.request.PassengerRequest;
import com.modsen.passengerservice.dto.response.ExceptionResponse;
import com.modsen.passengerservice.dto.response.MessageResponse;
import com.modsen.passengerservice.dto.response.PassengerResponse;
import com.modsen.passengerservice.dto.response.ValidationExceptionResponse;
import com.modsen.passengerservice.entity.Passenger;
import com.modsen.passengerservice.integration.IntegrationTestStructure;
import com.modsen.passengerservice.mapper.PassengerMapper;
import com.modsen.passengerservice.repository.PassengerRepository;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

import static com.modsen.passengerservice.util.Messages.*;
import static com.modsen.passengerservice.util.PassengerTestUtils.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@Sql(
        scripts = {
                "classpath:sql/passenger/delete-data.sql",
                "classpath:sql/passenger/insert-data.sql"
        }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PassengerIntegrationTest extends IntegrationTestStructure {
    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;
    @LocalServerPort
    private int port;

    @Test
    void findById_shouldReturnNotFoundResponse_whenPassengerNotExist() {
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
    void findById_shouldReturnPassengerResponse_whenPassengerExists() {
        Passenger passenger = passengerRepository.findById(DEFAULT_ID).get();
        PassengerResponse expected = passengerMapper.toPassengerResponse(passenger);

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .when()
                .get(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PassengerResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAll_whenValidParamsPassed() {
        Page<Passenger> passengerPage = passengerRepository.findAll(
                PageRequest.of(VALID_PAGE - 1, VALID_SIZE, Sort.by(VALID_ORDER_BY))
        );
        List<PassengerResponse> expected = passengerMapper.toPassengerResponseList(passengerPage);

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
                .extract().body().jsonPath().getList("passengers", PassengerResponse.class);

        assertThat(actual).isEqualTo(expected);
        assertThat(passengerRepository.findAll().size()).isEqualTo(3);
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
    void addPassenger_shouldReturnPassengerResponse_whenDataIsValidAndUnique() {
        PassengerRequest createRequest = getUniquePassengerRequest();

        PassengerResponse expected = PassengerResponse.builder()
                .id(NEW_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(UNIQUE_EMAIL)
                .phone(UNIQUE_PHONE)
                .rating(DEFAULT_RATING)
                .build();

        var actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post(DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(PassengerResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void addPassenger_shouldReturnConflictResponse_whenDataNotUnique() {
        PassengerRequest createRequest = getPassengerRequest();
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.CONFLICT)
                .message(PASSENGER_ALREADY_EXISTS_MESSAGE)
                .build();

        var actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post(DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void addPassenger_shouldReturnBadRequestResponse_whenDataNotValid() {
        PassengerRequest invalidRequest = PassengerRequest.builder()
                .name(INVALID_NAME)
                .surname(INVALID_SURNAME)
                .email(INVALID_EMAIL)
                .phone(INVALID_PHONE)
                .build();
        ValidationExceptionResponse expected = getPassengerValidationExceptionResponse();

        var actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .post(DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteById_shouldReturnNotFoundResponse_whenPassengerNotExist() {
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
    void deleteById_shouldReturnMessageResponse_whenPassengerExists() {
        MessageResponse expected = MessageResponse.builder()
                .message(String.format(DELETE_PASSENGER_MESSAGE, DEFAULT_ID))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .when()
                .delete(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(MessageResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void updateById_shouldReturnNotFoundResponse_whenPassengerNotExist() {
        PassengerRequest updateRequest = getUniquePassengerRequest();
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(String.format(NOT_FOUND_WITH_ID_MESSAGE, NOT_FOUND_ID))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, NOT_FOUND_ID)
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void updateById_shouldReturnPassengerResponse_whenDataIsValidAndUnique() {
        PassengerRequest updateRequest = getUniquePassengerRequest();
        PassengerResponse expected = PassengerResponse.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(UNIQUE_EMAIL)
                .phone(UNIQUE_PHONE)
                .rating(DEFAULT_RATING)
                .build();

        var actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .body(updateRequest)
                .when()
                .put(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PassengerResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void updateById_shouldReturnConflictResponse_whenDataNotUnique() {
        PassengerRequest updateRequest = getPassengerRequest();
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.CONFLICT)
                .message(PASSENGER_ALREADY_EXISTS_MESSAGE)
                .build();

        var actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .pathParam(ID_PARAM_NAME, 2L)
                .body(updateRequest)
                .when()
                .put(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

}
