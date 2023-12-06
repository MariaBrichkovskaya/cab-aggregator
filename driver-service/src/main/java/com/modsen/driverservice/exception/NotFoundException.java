package com.modsen.driverservice.exception;

import static com.modsen.driverservice.util.Messages.*;

public class NotFoundException extends RuntimeException {

    public NotFoundException(Long id) {
        super(String.format(NOT_FOUND_WITH_ID_MESSAGE, id));
    }
}
