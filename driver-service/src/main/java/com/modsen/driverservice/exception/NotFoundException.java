package com.modsen.driverservice.exception;

import java.util.UUID;

import static com.modsen.driverservice.util.Messages.*;

public class NotFoundException extends RuntimeException {

    public NotFoundException(UUID id) {
        super(String.format(NOT_FOUND_WITH_DRIVER_ID_MESSAGE, id));
    }
}
