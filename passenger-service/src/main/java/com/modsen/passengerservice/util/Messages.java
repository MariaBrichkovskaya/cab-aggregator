package com.modsen.passengerservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Messages {
    public final String VALIDATION_FAILED_MESSAGE = "Invalid request";

    public final String PASSENGER_ALREADY_EXISTS_MESSAGE = "Passenger already exists";
    public final String NOT_FOUND_WITH_ID_MESSAGE = "Passenger with id %d was not found";
    public final String DELETE_PASSENGER_MESSAGE = "Passenger with id %d was deleted";
    public final String PASSENGER_WITH_EMAIL_EXISTS_MESSAGE = "Passenger with email %s already exists";
    public final String PASSENGER_WITH_PHONE_EXISTS_MESSAGE = "Passenger with phone %s already exists";
    public final String INVALID_PAGE_MESSAGE = "Page request is not valid";
    public final String INVALID_SORTING_MESSAGE = "Sorting request is not valid. Acceptable parameters are: %s";
    public final int RETRYER_PERIOD = 100;
    public final int RETRYER_MAX_PERIOD = 1000;
    public final int RETRYER_MAX_ATTEMPTS = 5;
}