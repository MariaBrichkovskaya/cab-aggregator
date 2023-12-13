package com.modsen.paymentservice.exception;

import com.modsen.paymentservice.dto.response.ExceptionResponse;
import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PaymentHandler {
    @ExceptionHandler(value = {StripeException.class})
    public ResponseEntity<Object> handleStripeException(StripeException stripeException) {
        ExceptionResponse response =
                new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                        stripeException.getMessage()
                );
        return new ResponseEntity<>(response, response.getStatus());
    }
}
