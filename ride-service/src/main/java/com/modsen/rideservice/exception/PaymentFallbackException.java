package com.modsen.rideservice.exception;

public class PaymentFallbackException extends RuntimeException {
    public PaymentFallbackException(String message) {
        super(message);
    }
}
