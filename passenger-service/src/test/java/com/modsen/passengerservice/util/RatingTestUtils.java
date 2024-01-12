package com.modsen.passengerservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.passengerservice.dto.request.*;
import com.modsen.passengerservice.dto.response.*;
import com.modsen.passengerservice.entity.*;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.ResourceBundle;

import static com.modsen.passengerservice.util.Messages.*;
import static com.modsen.passengerservice.util.PassengerTestUtils.*;

@UtilityClass
public class RatingTestUtils {
    public final String RATING_PATH = "/api/v1/passengers/{id}/rating";
    public final String AVERAGE_RATING_PATH = "/api/v1/passengers/{id}/rating/average";
    private static final ResourceBundle validationMessages = ResourceBundle.getBundle("CustomValidationMessages");
    public final String DEFAULT_DRIVER_PATH = "/api/v1/drivers/";

    public ValidationExceptionResponse getRatingValidationExceptionResponse() {
        String driverId = validationMessages.getString("min.value.message");
        String score = validationMessages.getString("max.value.message");
        return ValidationExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(VALIDATION_FAILED_MESSAGE)
                .errors(Map.of(
                        "driverId", driverId,
                        "score", score
                ))
                .build();
    }

    public Rating getDefaultPassengerRating() {
        return Rating.builder().passenger(getDefaultPassenger())
                .score(DEFAULT_SCORE)
                .driverId(DEFAULT_ID).build();
    }

    public Rating getSavedPassengerRating() {
        return Rating.builder().passenger(getDefaultPassenger())
                .id(DEFAULT_ID)
                .score(DEFAULT_SCORE)
                .driverId(DEFAULT_ID).build();
    }

    public Rating getNewSavedPassengerRating() {
        return Rating.builder().passenger(getDefaultPassenger())
                .id(NEW_ID)
                .score(DEFAULT_SCORE)
                .driverId(DEFAULT_ID).build();
    }

    public PassengerRatingRequest getDefaultPassengerRatingRequest() {
        return PassengerRatingRequest.builder()
                .score(DEFAULT_SCORE)
                .driverId(DEFAULT_ID).build();
    }

    public PassengerRatingResponse getDefaultPassengerRatingResponse() {
        return PassengerRatingResponse.builder()
                .passengerId(DEFAULT_ID)
                .score(DEFAULT_SCORE).build();
    }

    public DriverResponse getDefaultDriverResponse() {
        return DriverResponse.builder().name(DEFAULT_NAME)
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING)
                .status("AVAILABLE")
                .build();
    }

    public static <T> String fromObjectToString(T object) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
