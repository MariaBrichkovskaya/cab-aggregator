package com.modsen.rideservice.exception;

public class DriverIsEmptyException extends RuntimeException{
    public DriverIsEmptyException(String message){
        super(message);
    }
}
