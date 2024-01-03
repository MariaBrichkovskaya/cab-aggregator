package com.modsen.passengerservice.util;

import com.modsen.passengerservice.dto.request.PassengerRequest;
import com.modsen.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.modsen.passengerservice.dto.response.PassengerResponse;
import com.modsen.passengerservice.entity.Passenger;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtils {
    public final long DEFAULT_ID = 1L;
    public final String DEFAULT_NAME = "Name";
    public final String DEFAULT_SURNAME = "Surname";
    public final String DEFAULT_EMAIL = "123@example.com";
    public final String DEFAULT_PHONE = "80291234567";
    public final double DEFAULT_RATING = 5.0;


    public final int INVALID_PAGE = -1;
    public final int INVALID_SIZE = -1;
    public final String INVALID_ORDER_BY = "qwerty";

    public Passenger getDefaultPassenger() {
        return Passenger.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
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
    public AveragePassengerRatingResponse getDefaultRating(){
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



}
