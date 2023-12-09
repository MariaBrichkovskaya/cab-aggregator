package com.modsen.rideservice.exception;

import static com.modsen.rideservice.util.Messages.*;

public class NotFoundException extends RuntimeException {

    public NotFoundException(Long id) {
        super(String.format(NOT_FOUND_WITH_ID_MESSAGE, id));
    }
    public NotFoundException(String message){
        super(message);
    }
}
