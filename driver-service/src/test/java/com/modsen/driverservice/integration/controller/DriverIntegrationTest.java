package com.modsen.driverservice.integration.controller;


import com.modsen.driverservice.dto.request.DriverRequest;
import com.modsen.driverservice.dto.response.DriverResponse;
import com.modsen.driverservice.dto.response.ExceptionResponse;
import com.modsen.driverservice.dto.response.MessageResponse;
import com.modsen.driverservice.dto.response.ValidationExceptionResponse;
import com.modsen.driverservice.entity.Driver;
import com.modsen.driverservice.enums.Status;
import com.modsen.driverservice.integration.IntegrationTestStructure;
import com.modsen.driverservice.mapper.DriverMapper;
import com.modsen.driverservice.repository.DriverRepository;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static com.modsen.driverservice.util.DriverTestUtils.*;
import static com.modsen.driverservice.util.Messages.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DriverIntegrationTest extends IntegrationTestStructure {
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    @LocalServerPort
    private int port;

    @Test
    void findById_shouldReturnNotFoundResponse_whenDriverNotExist() {
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(String.format(NOT_FOUND_WITH_DRIVER_ID_MESSAGE, NOT_FOUND_ID))
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
        Driver driver = driverRepository.findById(DEFAULT_ID).get();
        DriverResponse expected = driverMapper.toDriverResponse(driver);

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .when()
                .get(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(DriverResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAll_whenValidParamsPassed() {
        Page<Driver> driverPage = driverRepository.findAll(
                PageRequest.of(VALID_PAGE - 1, VALID_SIZE, Sort.by(VALID_ORDER_BY))
        );
        List<DriverResponse> expected = driverMapper.toDriverResponseList(driverPage);

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
                .extract().body().jsonPath().getList("drivers", DriverResponse.class);

        assertThat(actual).isEqualTo(expected);
        assertThat(driverRepository.findAll().size()).isEqualTo(3);
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
    void addDriver_shouldReturnDriverResponse_whenDataIsValidAndUnique() {
        DriverRequest createRequest = getUniqueRequest();

        DriverResponse expected = DriverResponse.builder()
                .id(NEW_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(UNIQUE_PHONE)
                .rating(DEFAULT_RATING)
                .status(Status.AVAILABLE)
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
                .as(DriverResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void addDriver_shouldReturnConflictResponse_whenDataNotUnique() {
        DriverRequest createRequest = getDriverRequest();
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.CONFLICT)
                .message(String.format(DRIVER_WITH_PHONE_EXISTS_MESSAGE, createRequest.getPhone()))
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
    void addDriver_shouldReturnBadRequestResponse_whenDataNotValid() {
        DriverRequest invalidRequest = DriverRequest.builder()
                .name(INVALID_NAME)
                .surname(INVALID_SURNAME)
                .phone(INVALID_PHONE)
                .build();
        ValidationExceptionResponse expected = getDriverValidationExceptionResponse();

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
    void deleteById_shouldReturnNotFoundResponse_whenDriverNotExist() {
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(String.format(NOT_FOUND_WITH_DRIVER_ID_MESSAGE, NOT_FOUND_ID))
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
        MessageResponse expected = MessageResponse.builder()
                .message(String.format(DELETE_DRIVER_MESSAGE, DEFAULT_ID))
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
    void updateById_shouldReturnNotFoundResponse_whenDriverNotExist() {
        DriverRequest updateRequest = getUniqueRequest();
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(String.format(NOT_FOUND_WITH_DRIVER_ID_MESSAGE, NOT_FOUND_ID))
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
    void updateById_shouldReturnDriverResponse_whenDataIsValidAndUnique() {
        DriverRequest updateRequest = getUniqueRequest();
        DriverResponse expected = DriverResponse.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(UNIQUE_PHONE)
                .rating(DEFAULT_RATING)
                .status(Status.AVAILABLE)
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
                .as(DriverResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void updateById_shouldReturnConflictResponse_whenDataNotUnique() {
        DriverRequest updateRequest = getDriverRequest();
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.CONFLICT)
                .message(String.format(DRIVER_WITH_PHONE_EXISTS_MESSAGE, updateRequest.getPhone()))
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

    @Test
    void changeStatus_shouldReturnNotFoundResponse_whenDriverNotExist() {
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(String.format(NOT_FOUND_WITH_DRIVER_ID_MESSAGE, NOT_FOUND_ID))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, NOT_FOUND_ID)
                .contentType(ContentType.JSON)
                .when()
                .put(CHANGE_STATUS_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void changeStatus_shouldReturnMessageResponse() {
        MessageResponse expected = MessageResponse.builder()
                .message(String.format(EDIT_DRIVER_STATUS_MESSAGE, DEFAULT_ID))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .contentType(ContentType.JSON)
                .when()
                .put(CHANGE_STATUS_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(MessageResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAvailable_whenValidParamsPassed() {
        Page<Driver> driverPage = driverRepository.findByStatus(
                Status.AVAILABLE, PageRequest.of(VALID_PAGE - 1, VALID_SIZE, Sort.by(VALID_ORDER_BY))
        );
        List<DriverResponse> expected = driverMapper.toDriverResponseList(driverPage);

        var actual = given()
                .port(port)
                .params(Map.of(
                        PAGE_PARAM_NAME, VALID_PAGE,
                        SIZE_PARAM_NAME, VALID_SIZE,
                        ORDER_BY_PARAM_NAME, VALID_ORDER_BY)
                )
                .when()
                .get(AVAILABLE_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().body().jsonPath().getList("drivers", DriverResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAvailable_shouldReturnBadRequestResponse_whenInvalidPagePassed() {
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
                .get(AVAILABLE_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAvailable_shouldReturnBadRequestResponse_whenInvalidSizePassed() {
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
                .get(AVAILABLE_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAvailable_shouldReturnBadRequestResponse_whenInvalidOrderByParamPassed() {
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
                .get(AVAILABLE_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

}
