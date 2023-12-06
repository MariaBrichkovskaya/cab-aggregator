package com.modsen.driverservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Messages {
    public final String VALIDATION_FAILED_MESSAGE = "Invalid request";

    public final String DRIVER_ALREADY_EXISTS_MESSAGE = "Driver already exists";
    public final String NOT_FOUND_WITH_ID_MESSAGE = "Driver with id %d was not found";
    public final String DRIVER_WITH_PHONE_EXISTS_MESSAGE = "Driver with phone %s already exists";
    public final String INVALID_PAGE_MESSAGE="Page request is not valid";
    public final String INVALID_SORTING_MESSAGE="Sorting request is not valid. Acceptable parameters are: %s";
}