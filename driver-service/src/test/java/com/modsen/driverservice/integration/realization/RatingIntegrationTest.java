package com.modsen.driverservice.integration.realization;


import com.modsen.driverservice.dto.request.DriverRatingRequest;
import com.modsen.driverservice.dto.response.AverageDriverRatingResponse;
import com.modsen.driverservice.dto.response.DriverRatingResponse;
import com.modsen.driverservice.dto.response.ExceptionResponse;
import com.modsen.driverservice.dto.response.PassengerResponse;
import com.modsen.driverservice.dto.response.ValidationExceptionResponse;
import com.modsen.driverservice.integration.IntegrationTestStructure;
import com.modsen.driverservice.repository.DriverRepository;
import com.modsen.driverservice.repository.RatingRepository;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.modsen.driverservice.util.DriverTestUtils.*;
import static com.modsen.driverservice.util.Messages.*;
import static com.modsen.driverservice.util.RatingTestUtils.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@AutoConfigureWireMock(port = 9001)
public class RatingIntegrationTest extends IntegrationTestStructure {

    private final RatingRepository ratingRepository;

    private final DriverRepository driverRepository;

    private final ModelMapper modelMapper;
    @LocalServerPort
    private int port;


    @Test
    void getAverageRating_shouldReturnNotFoundResponse_whenDriverNotExist() {
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(String.format(NOT_FOUND_WITH_DRIVER_ID_MESSAGE, NOT_FOUND_ID)).build();

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
    void getAverageRating_shouldReturnAverageDriverRatingResponse_whenDriverExists() {
        AverageDriverRatingResponse expected = AverageDriverRatingResponse.builder()
                .driverId(DEFAULT_ID)
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
                .as(AverageDriverRatingResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void addRating_shouldReturnBadRequestResponse_whenDataNotValid() {
        DriverRatingRequest invalidRequest = DriverRatingRequest.builder()
                .score(6)
                .passengerId(0L)
                .build();
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
    void rateDriver_shouldReturnRatingResponse_whenDataValid() {
        DriverRatingRequest request = getDefaultDriverRatingRequest();
        PassengerResponse passengerResponse = getDefaultPassengerResponse();
        stubFor(get(urlPathMatching(DEFAULT_PASSENGER_PATH + DEFAULT_ID))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("content-type", "application/json")
                        .withBody(fromObjectToString(passengerResponse)))
        );
        DriverRatingResponse expected = getDefaultDriverRatingResponse();
        expected.setId(DEFAULT_ID);
        expected.setPassengerResponse(passengerResponse);

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
                .as(DriverRatingResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getRatingsById_shouldReturnDriverRatingResponse() {
        List<DriverRatingResponse> expected = getAverageRating();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .when()
                .get(RATING_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().body().jsonPath().getList("driverRatings", DriverRatingResponse.class);

        assertThat(actual).isEqualTo(expected);
        assertThat(driverRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    void getRatingsById_shouldReturnNotFoundResponse_whenDriverNotExist() {
        ExceptionResponse expected = ExceptionResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(String.format(NOT_FOUND_WITH_DRIVER_ID_MESSAGE, NOT_FOUND_ID))
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

    private List<DriverRatingResponse> getAverageRating() {
        List<DriverRatingResponse> expected = ratingRepository.getRatingsByDriverId(DEFAULT_ID).stream()
                .map(rating -> {
                    DriverRatingResponse response = modelMapper.map(rating, DriverRatingResponse.class);
                    response.setPassengerResponse(getDefaultPassengerResponse());
                    return response;
                }).toList();
        PassengerResponse driverResponse = getDefaultPassengerResponse();
        for (int i = 1; i <= expected.size(); i++) {
            stubFor(get(urlPathMatching(DEFAULT_PASSENGER_PATH + i))
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
