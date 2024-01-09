package com.modsen.driverservice.util;

import com.modsen.driverservice.dto.request.DriverRatingRequest;
import com.modsen.driverservice.dto.request.DriverRequest;
import com.modsen.driverservice.dto.request.RideRequest;
import com.modsen.driverservice.dto.response.AverageDriverRatingResponse;
import com.modsen.driverservice.dto.response.DriverRatingResponse;
import com.modsen.driverservice.dto.response.DriverResponse;
import com.modsen.driverservice.dto.response.PassengerResponse;
import com.modsen.driverservice.entity.Driver;
import com.modsen.driverservice.entity.Rating;
import com.modsen.driverservice.enums.Status;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class TestUtils {
    public final long DEFAULT_ID = 1L;
    public final long NEW_ID = 2L;
    public final String DEFAULT_NAME = "Name";
    public final String DEFAULT_SURNAME = "Surname";
    public final String DEFAULT_PHONE = "80291234567";
    public final String DEFAULT_EMAIL = "123@gmail.com";
    public final double DEFAULT_RATING = 5.0;
    public final String UNIQUE_PHONE = "80291237567";
    public final Integer DEFAULT_SCORE = 4;
    public final double DEFAULT_AVERAGE_RATING = 4.0;
    public final int INVALID_PAGE = -1;
    public final int VALID_PAGE = 1;
    public final int INVALID_SIZE = -1;
    public final int VALID_SIZE = 10;
    public final String INVALID_ORDER_BY = "qwerty";
    public final String VALID_ORDER_BY = "id";

    public Driver getDefaultDriver() {
        return Driver.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(DEFAULT_PHONE)
                .status(Status.AVAILABLE)
                .build();
    }

    public Driver getSecondDriver() {
        return Driver.builder()
                .id(NEW_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(UNIQUE_PHONE)
                .status(Status.AVAILABLE)
                .build();
    }

    public DriverResponse getDefaultDriverResponse() {
        return DriverResponse.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING)
                .build();
    }

    public AverageDriverRatingResponse getDefaultRating() {
        return AverageDriverRatingResponse.builder()
                .driverId(DEFAULT_ID)
                .averageRating(DEFAULT_RATING)
                .build();
    }

    public Driver getNotSavedDriver() {
        return Driver.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(DEFAULT_PHONE)
                .build();
    }

    public DriverRequest getDriverRequest() {
        return DriverRequest.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(DEFAULT_PHONE)
                .build();
    }

    public Driver getUpdateDriver() {
        return Driver.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(UNIQUE_PHONE)
                .build();
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
        return Rating.builder()
                .driver(getDefaultDriver())
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

    public PassengerResponse getDefaultPassengerResponse() {
        return PassengerResponse.builder()
                .name(DEFAULT_NAME)
                .id(DEFAULT_ID)
                .name(DEFAULT_SURNAME)
                .phone(DEFAULT_PHONE)
                .email(DEFAULT_EMAIL)
                .build();
    }

    public List<DriverResponse> getDefaultDriversList() {
        return List.of(getDefaultDriverResponse());

    }

    public RideRequest getDefaultRideRequest() {
        return RideRequest.builder()
                .id(DEFAULT_ID)
                .build();
    }

}
