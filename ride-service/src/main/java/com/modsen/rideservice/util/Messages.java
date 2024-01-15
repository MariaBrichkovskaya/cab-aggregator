package com.modsen.rideservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Messages {
    public final String VALIDATION_FAILED_MESSAGE = "Invalid request";
    public final String NOT_FOUND_WITH_ID_MESSAGE = "Ride with id %d was not found";
    public final String INVALID_PAGE_MESSAGE = "Page request is not valid";
    public final String INVALID_SORTING_MESSAGE = "Sorting request is not valid. Acceptable parameters are: %s";
    public final String EMPTY_DRIVER_MESSAGE = "Driver is empty";
    public final String DELETE_MESSAGE = "Ride with id %d was deleted";
    public final String ALREADY_FINISHED_MESSAGE = "Ride already finished";
    public final String CURRENCY = "BYN";
    public final int RETRYER_PERIOD = 100;
    public final int RETRYER_MAX_PERIOD = 1000;
    public final int RETRYER_MAX_ATTEMPTS = 5;
}