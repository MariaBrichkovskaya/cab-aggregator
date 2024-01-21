package com.modsen.driverservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.driverservice.dto.request.DriverRatingRequest;
import com.modsen.driverservice.dto.response.DriverRatingResponse;
import com.modsen.driverservice.dto.response.ValidationExceptionResponse;
import com.modsen.driverservice.entity.Rating;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static com.modsen.driverservice.util.DriverTestUtils.*;
import static com.modsen.driverservice.util.Messages.*;

@UtilityClass
public class RatingTestUtils {
    public final String RATING_PATH = "/api/v1/drivers/{id}/rating";
    public final String AVERAGE_RATING_PATH = "/api/v1/drivers/{id}/rating/average";
    private static final ResourceBundle validationMessages = ResourceBundle.getBundle("CustomValidationMessages");
    public final String DEFAULT_PASSENGER_PATH = "/api/v1/passengers/";

    public ValidationExceptionResponse getRatingValidationExceptionResponse() {
        String passengerId = validationMessages.getString("min.value.message");
        String score = validationMessages.getString("max.value.message");
        return ValidationExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(VALIDATION_FAILED_MESSAGE)
                .errors(Map.of(
                        "passengerId", passengerId,
                        "score", score
                ))
                .build();
    }

    public List<Rating> getDefaultRatings() {
        return List.of(getDefaultDriverRating(), getNewSavedDriverRating());
    }

    public Rating getDefaultDriverRating() {
        return Rating.builder()
                .driver(getDefaultDriver())
                .score(DEFAULT_SCORE)
                .passengerId(DEFAULT_ID)
                .build();
    }

    public Rating getSavedDriverRating() {
        return Rating.builder()
                .driver(getDefaultDriver())
                .id(DEFAULT_ID)
                .score(DEFAULT_SCORE)
                .passengerId(DEFAULT_ID)
                .build();
    }

    public Rating getNewSavedDriverRating() {
        return Rating.builder().driver(getDefaultDriver())
                .id(NEW_ID)
                .score(DEFAULT_SCORE)
                .passengerId(DEFAULT_ID)
                .build();
    }

    public DriverRatingRequest getDefaultDriverRatingRequest() {
        return DriverRatingRequest.builder()
                .score(DEFAULT_SCORE)
                .passengerId(DEFAULT_ID)
                .build();
    }

    public DriverRatingResponse getDefaultDriverRatingResponse() {
        return DriverRatingResponse.builder()
                .driverId(DEFAULT_ID)
                .score(DEFAULT_SCORE)
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
