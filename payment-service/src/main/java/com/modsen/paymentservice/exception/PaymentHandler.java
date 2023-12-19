package com.modsen.paymentservice.exception;

import com.modsen.paymentservice.dto.response.ExceptionResponse;
import com.modsen.paymentservice.dto.response.ValidationExceptionResponse;
import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class PaymentHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationExceptionResponse handleMethodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException) {
        String VALIDATION_FAILED_MESSAGE = "Invalid request";
        Map<String, String> errors = methodArgumentNotValidException.getBindingResult().getAllErrors().stream()
                .map(error -> (FieldError) error)
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        return new ValidationExceptionResponse(HttpStatus.BAD_REQUEST, VALIDATION_FAILED_MESSAGE, errors);
    }

    @ExceptionHandler(value = {BalanceException.class})
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    public ExceptionResponse handleBalanceException(BalanceException balanceException) {
        return new ExceptionResponse(HttpStatus.PAYMENT_REQUIRED, balanceException.getMessage());
    }

    @ExceptionHandler(value = {StripeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleStripeException(StripeException stripeException) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, stripeException.getMessage());
    }

    @ExceptionHandler(value = {AlreadyExistsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleAlreadyExistsException(AlreadyExistsException alreadyExistsException) {
        return new ExceptionResponse(HttpStatus.BAD_REQUEST, alreadyExistsException.getMessage());
    }

    @ExceptionHandler(value = {NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleNotFoundException(NotFoundException notFoundException) {
        return new ExceptionResponse(HttpStatus.NOT_FOUND, notFoundException.getMessage());
    }
}

