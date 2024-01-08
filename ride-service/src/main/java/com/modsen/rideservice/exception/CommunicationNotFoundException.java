package com.modsen.rideservice.exception;

public class CommunicationNotFoundException extends RuntimeException{
    public CommunicationNotFoundException(String message) {
        super( message);
    }
}
