package com.modsen.passengerservice.integration;

import com.modsen.passengerservice.dto.response.*;
import com.modsen.passengerservice.entity.*;
import com.modsen.passengerservice.mapper.*;
import com.modsen.passengerservice.repository.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.Map;

import static com.modsen.passengerservice.util.Messages.*;
import static com.modsen.passengerservice.util.TestUtils.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(
        scripts = {
                "classpath:sql/delete-data.sql"
                , "classpath:sql/insert-data.sql"
        }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)

public class PassengerIntegrationTest {
    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private PassengerMapper passengerMapper;
    @LocalServerPort
    private int port;
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("postgresql.driver", postgres::getDriverClassName);
    }


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
                .get(FIND_BY_ID_PATH)
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
                .get(FIND_BY_ID_PATH)
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
                        ORDER_BY_PARAM_NAME, VALID_ORDER_BY
                ))
                .when()
                .get(FIND_ALL_PATH)
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
                        ORDER_BY_PARAM_NAME, VALID_ORDER_BY
                ))
                .when()
                .get(FIND_ALL_PATH)
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
                .get(FIND_ALL_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getPassengerPage_shouldReturnBadRequestResponse_whenInvalidOrderByParamPassed() {
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
                .get(FIND_ALL_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }


}
