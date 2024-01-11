package com.modsen.passengerservice.util;

import com.modsen.passengerservice.dto.request.*;
import com.modsen.passengerservice.dto.response.*;
import com.modsen.passengerservice.entity.*;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.modsen.passengerservice.util.Messages.*;

@UtilityClass
public class TestUtils {
    public final long DEFAULT_ID = 1L;
    public final long NEW_ID = 2L;
    public final String DEFAULT_NAME = "Name";
    public final String DEFAULT_SURNAME = "Surname";
    public final String DEFAULT_EMAIL = "123@example.com";
    public final String DEFAULT_PHONE = "80291234567";
    public final double DEFAULT_RATING = 5.0;
    public final String UNIQUE_EMAIL = "12356@example.com";
    public final String UNIQUE_PHONE = "80291237567";
    public final Integer DEFAULT_SCORE = 4;
    public final double DEFAULT_AVERAGE_RATING = 4.0;
    public final int INVALID_PAGE = -1;
    public final int VALID_PAGE = 1;
    public final int INVALID_SIZE = -1;
    public final int VALID_SIZE = 10;
    public final String INVALID_ORDER_BY = "qwerty";
    public final String VALID_ORDER_BY = "id";
    public final long NOT_FOUND_ID = 1000L;
    public final String ID_PARAM_NAME = "id";
    public final String FIND_BY_ID_PATH = "/api/v1/passengers/{id}";
    public final String PAGE_PARAM_NAME = "page";
    public final String SIZE_PARAM_NAME = "size";
    public final String ORDER_BY_PARAM_NAME = "order_by";
    public final String FIND_ALL_PATH = "/api/v1/passengers";

    public Passenger getDefaultPassenger() {
        return Passenger.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .build();
    }

    public Passenger getSecondPassenger() {
        return Passenger.builder()
                .id(NEW_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(UNIQUE_EMAIL)
                .phone(UNIQUE_PHONE)
                .build();
    }

    public PassengerResponse getDefaultPassengerResponse() {
        return PassengerResponse.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING)
                .build();
    }

    public AveragePassengerRatingResponse getDefaultRating() {
        return AveragePassengerRatingResponse.builder().passengerId(DEFAULT_ID)
                .averageRating(DEFAULT_RATING).build();
    }

    public Passenger getNotSavedPassenger() {
        return Passenger.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .build();
    }

    public PassengerRequest getPassengerRequest() {
        return PassengerRequest.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .build();
    }

    public Passenger getUpdatePassenger() {
        return Passenger.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(UNIQUE_EMAIL)
                .phone(UNIQUE_PHONE)
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
                .name(DEFAULT_SURNAME)
                .phone(DEFAULT_PHONE)
                .build();
    }

    public String getInvalidSortingMessage() {
        List<String> fieldNames = Arrays.stream(PassengerResponse.class.getDeclaredFields())
                .map(Field::getName)
                .toList();

        return String.format(INVALID_SORTING_MESSAGE, fieldNames);
    }

}
