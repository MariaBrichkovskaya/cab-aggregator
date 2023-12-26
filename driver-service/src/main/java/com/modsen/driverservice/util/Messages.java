package com.modsen.driverservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Messages {
    public static final String VALIDATION_FAILED_MESSAGE = "Invalid request";

    public static final String NOT_FOUND_WITH_DRIVER_ID_MESSAGE = "Driver with id %d was not found";
    public static final String DRIVER_WITH_PHONE_EXISTS_MESSAGE = "Driver with phone %s already exists";
    public static final String INVALID_PAGE_MESSAGE="Page request is not valid";
    public static final String INVALID_SORTING_MESSAGE="Sorting request is not valid. Acceptable parameters are: %s";
}