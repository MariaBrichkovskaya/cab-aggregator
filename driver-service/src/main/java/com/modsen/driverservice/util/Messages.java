package com.modsen.driverservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Messages {
    public final String VALIDATION_FAILED_MESSAGE = "Invalid request";

    public final String NOT_FOUND_WITH_DRIVER_ID_MESSAGE = "Driver with id %d was not found";
    public final String DRIVER_WITH_PHONE_EXISTS_MESSAGE = "Driver with phone %s already exists";
    public final String INVALID_PAGE_MESSAGE = "Page request is not valid";
    public final String INVALID_SORTING_MESSAGE = "Sorting request is not valid. Acceptable parameters are: %s";
    public final String DELETE_DRIVER_MESSAGE = "Driver with %d was deleted";
    public final String EDIT_DRIVER_STATUS_MESSAGE = "Status for driver with %d was changed";
    public final int RETRYER_PERIOD = 100;
    public final int RETRYER_MAX_PERIOD = 1000;
    public final int RETRYER_MAX_ATTEMPTS = 5;
}