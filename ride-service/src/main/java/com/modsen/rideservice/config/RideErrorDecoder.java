package com.modsen.rideservice.config;


import com.modsen.rideservice.exception.BalanceException;
import com.modsen.rideservice.exception.NotFoundException;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;


public class RideErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        FeignException exception = FeignException.errorStatus(methodKey, response);
        int status = response.status();
        return switch (status) {
            case 402 -> new BalanceException(exception.getMessage());
            case 404 -> new NotFoundException(exception.getMessage());
            case 500, 501, 502 -> new RetryableException(
                    response.status(),
                    exception.getMessage(),
                    response.request().httpMethod(),
                    exception,
                    (Long) null,
                    response.request());
            default -> exception;
        };
    }


}

