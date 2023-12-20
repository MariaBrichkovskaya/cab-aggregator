package com.modsen.passengerservice.config;

import feign.RetryableException;
import feign.Retryer;
import org.springframework.stereotype.Component;

@Component
public class DriverRetryer implements Retryer {
    @Override
    public void continueOrPropagate(RetryableException e) {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    @Override
    public Retryer clone() {
        return new DriverRetryer();
    }

}