package com.modsen.rideservice.exception.handler;

import com.modsen.rideservice.dto.response.ExceptionResponse;
import com.modsen.rideservice.dto.response.ValidationExceptionResponse;
import com.modsen.rideservice.exception.*;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;


import static com.modsen.rideservice.util.Messages.*;

@RestControllerAdvice
public class RideHandler {
    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(NotFoundException notFoundException) {
        ExceptionResponse response =
                new ExceptionResponse(HttpStatus.NOT_FOUND,
                        notFoundException.getMessage()
                );
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(value = {AlreadyFinishedRideException.class})
    public ResponseEntity<Object> handleAlreadyFinishedRideException(AlreadyFinishedRideException alreadyFinishedRideException) {
        ExceptionResponse response =
                new ExceptionResponse(HttpStatus.CONFLICT,
                        alreadyFinishedRideException.getMessage()
                );
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(value = {InvalidRequestException.class})
    public ResponseEntity<Object> handleNotFoundException(InvalidRequestException invalidRequestException) {
        ExceptionResponse response =
                new ExceptionResponse(HttpStatus.BAD_REQUEST,
                        invalidRequestException.getMessage()
                );
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(value = {DriverIsEmptyException.class})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ExceptionResponse handleDriverIsEmptyException(DriverIsEmptyException driverIsEmptyException) {
        return new ExceptionResponse(HttpStatus.NO_CONTENT,
                driverIsEmptyException.getMessage());

    }

    @ExceptionHandler(value = {FeignException.class})
    public ResponseEntity<ExceptionResponse> handleFeignException(FeignException feignException) {

        ExceptionResponse response = new ExceptionResponse(HttpStatus.valueOf(feignException.status()),
                feignException.getMessage()
        );
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException) {
        var errors = new HashMap<String, String>();
        methodArgumentNotValidException.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ValidationExceptionResponse response = new ValidationExceptionResponse(HttpStatus.BAD_REQUEST, VALIDATION_FAILED_MESSAGE, errors);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(value = {CommunicationNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleNotFoundException(CommunicationNotFoundException communicationNotFoundException) {
        ExceptionResponse response =
                new ExceptionResponse(HttpStatus.BAD_REQUEST,
                        communicationNotFoundException.getMessage()
                );
        return new ResponseEntity<>(response, response.getStatus());
    }


}

