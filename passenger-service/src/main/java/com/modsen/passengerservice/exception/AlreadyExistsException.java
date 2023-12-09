package com.modsen.passengerservice.exception;

import lombok.Getter;

import java.util.Map;

import static com.modsen.passengerservice.util.Messages.*;
@Getter
public class AlreadyExistsException extends RuntimeException{
    private final Map<String, String> errors;

    public AlreadyExistsException(Map<String, String> errors) {
        super(PASSENGER_ALREADY_EXISTS_MESSAGE);
        this.errors = errors;
    }
}
