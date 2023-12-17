package com.modsen.paymentservice.exception;

import com.modsen.paymentservice.dto.response.ExceptionResponse;
import com.modsen.paymentservice.dto.response.ValidationExceptionResponse;
import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

@ControllerAdvice
public class PaymentHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException) {
        var errors = new HashMap<String, String>();
        methodArgumentNotValidException.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        String VALIDATION_FAILED_MESSAGE = "Invalid request";
        ValidationExceptionResponse response = new ValidationExceptionResponse(HttpStatus.BAD_REQUEST, VALIDATION_FAILED_MESSAGE, errors);
        return new ResponseEntity<>(response, response.getStatus());
    }
    @ExceptionHandler(value = {BalanceException.class})
    public ResponseEntity<Object> handleBalanceException(BalanceException balanceException) {
        ExceptionResponse response =
                new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                        balanceException.getMessage()
                );
        return new ResponseEntity<>(response, response.getStatus());
    }
    @ExceptionHandler(value = {StripeException.class})
    public ResponseEntity<Object> handleStripeException(StripeException stripeException) {
        ExceptionResponse response =
                new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                        stripeException.getMessage()
                );
        return new ResponseEntity<>(response, response.getStatus());
    }
    @ExceptionHandler(value = {AlreadyExistsException.class})
    public ResponseEntity<Object> handleStripeException(AlreadyExistsException alreadyExistsException) {
        ExceptionResponse response =
                new ExceptionResponse(HttpStatus.BAD_REQUEST,
                        alreadyExistsException.getMessage()
                );
        return new ResponseEntity<>(response, response.getStatus());
    }

}
