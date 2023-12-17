package com.modsen.paymentservice.exception;

public class BalanceException extends RuntimeException{
    public BalanceException(String message){
        super(message);
    }
}
