package com.modsen.passengerservice.exception;

import java.util.UUID;

import static com.modsen.passengerservice.util.Messages.*;

public class NotFoundException extends RuntimeException {

    public NotFoundException(UUID id) {
        super(String.format(NOT_FOUND_WITH_ID_MESSAGE, id));
    }
}
