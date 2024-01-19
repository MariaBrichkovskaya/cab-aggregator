package com.modsen.passengerservice.integration.controller;

import com.modsen.passengerservice.dto.request.PassengerRatingRequest;
import com.modsen.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.modsen.passengerservice.dto.response.DriverResponse;
import com.modsen.passengerservice.dto.response.ExceptionResponse;
import com.modsen.passengerservice.dto.response.PassengerRatingResponse;
import com.modsen.passengerservice.dto.response.ValidationExceptionResponse;
import com.modsen.passengerservice.integration.IntegrationTestStructure;
import com.modsen.passengerservice.repository.PassengerRepository;
import com.modsen.passengerservice.repository.RatingRepository;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.modsen.passengerservice.util.Messages.*;
import static com.modsen.passengerservice.util.PassengerTestUtils.*;
import static com.modsen.passengerservice.util.RatingTestUtils.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Sql(scripts = {"classpath:sql/passenger/delete-data.sql",
        "classpath:sql/passenger/insert-data.sql",
        "classpath:sql/rating/delete-data.sql",
        "classpath:sql/rating/insert-data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureWireMock(port = 9002)
public class RatingIntegrationTest extends IntegrationTestStructure {

    private final RatingRepository ratingRepository;

    private final PassengerRepository passengerRepository;

    private final ModelMapper modelMapper;
    @LocalServerPort
    private int port;


    @Test
    void getAverageRating_shouldReturnNotFoundResponse_whenPassengerNotExist() {
        ExceptionResponse expected = ExceptionResponse.builder().status(HttpStatus.NOT_FOUND).message(String.format(NOT_FOUND_WITH_ID_MESSAGE, NOT_FOUND_ID)).build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, NOT_FOUND_ID)
                .when()
                .get(AVERAGE_RATING_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAverageRating_shouldReturnAveragePassengerRatingResponse_whenPassengerExists() {
        AveragePassengerRatingResponse expected = AveragePassengerRatingResponse.builder()
                .passengerId(DEFAULT_ID)
                .averageRating(DEFAULT_RATING)
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .when()
                .get(AVERAGE_RATING_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(AveragePassengerRatingResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void addRating_shouldReturnBadRequestResponse_whenDataNotValid() {
        PassengerRatingRequest invalidRequest = PassengerRatingRequest.builder().score(6).driverId(0L).build();
        ValidationExceptionResponse expected = getRatingValidationExceptionResponse();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .contentType(ContentType.JSON)
                .body(invalidRequest).when()
                .post(RATING_PATH).then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void ratePassenger_shouldReturnRatingResponse_whenDataValid() {
        PassengerRatingRequest request = getDefaultPassengerRatingRequest();
        DriverResponse driverResponse = getDefaultDriverResponse();
        stubFor(get(urlPathMatching(DEFAULT_DRIVER_PATH + DEFAULT_ID))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("content-type", "application/json")
                        .withBody(fromObjectToString(driverResponse)))
        );
        PassengerRatingResponse expected = getDefaultPassengerRatingResponse();
        expected.setId(DEFAULT_ID);
        expected.setDriverResponse(driverResponse);

        var actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .when()
                .post(RATING_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PassengerRatingResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getRatingsById_shouldReturnPassengerRatingResponse() {
        List<PassengerRatingResponse> expected = getAverageRating();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .when()
                .get(RATING_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().body().jsonPath().getList("passengerRatings", PassengerRatingResponse.class);

        assertThat(actual).isEqualTo(expected);
        assertThat(passengerRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    void getRatingsById_shouldReturnNotFoundResponse_whenPassengerNotExist() {
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(String.format(NOT_FOUND_WITH_ID_MESSAGE, NOT_FOUND_ID))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, NOT_FOUND_ID)
                .when()
                .get(RATING_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    private List<PassengerRatingResponse> getAverageRating() {
        List<PassengerRatingResponse> expected = ratingRepository.getRatingsByPassengerId(DEFAULT_ID).stream()
                .map(rating -> {
                    PassengerRatingResponse response = modelMapper.map(rating, PassengerRatingResponse.class);
                    response.setDriverResponse(getDefaultDriverResponse());
                    return response;
                }).toList();
        DriverResponse driverResponse = getDefaultDriverResponse();
        for (int i = 1; i <= expected.size(); i++) {
            stubFor(get(urlPathMatching(DEFAULT_DRIVER_PATH + i))
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.OK.value())
                            .withHeader("content-type", "application/json")
                            .withBody(fromObjectToString(driverResponse))
                    )
            );
        }
        return expected;
    }


}
