package com.modsen.driverservice.exception;

public class DriverIsUnavailableException extends RuntimeException {
    public DriverIsUnavailableException(String message) {
        super(message);
    }
}
