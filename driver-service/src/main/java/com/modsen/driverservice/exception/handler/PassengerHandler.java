package com.modsen.driverservice.exception.handler;


import com.modsen.driverservice.dto.response.AlreadyExistsResponse;
import com.modsen.driverservice.dto.response.ExceptionResponse;
import com.modsen.driverservice.dto.response.ValidationExceptionResponse;
import com.modsen.driverservice.exception.AlreadyExistsException;
import com.modsen.driverservice.exception.InvalidRequestException;
import com.modsen.driverservice.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

import static com.modsen.driverservice.util.Messages.*;

@ControllerAdvice
public class PassengerHandler {
    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(NotFoundException notFoundException){
        ExceptionResponse response=
                new ExceptionResponse( HttpStatus.NOT_FOUND,
                        notFoundException.getMessage()
                );
        return new ResponseEntity<>(response,response.getStatus());
    }

    @ExceptionHandler(value = {InvalidRequestException.class})
    public ResponseEntity<Object> handleNotFoundException(InvalidRequestException invalidRequestException){
        ExceptionResponse response=
                new ExceptionResponse( HttpStatus.BAD_REQUEST,
                        invalidRequestException.getMessage()
                );
        return new ResponseEntity<>(response,response.getStatus());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public AlreadyExistsResponse handleDriverAlreadyExists(AlreadyExistsException alreadyExistsException) {
        return AlreadyExistsResponse.builder()
                .status(HttpStatus.CONFLICT)
                .message(alreadyExistsException.getMessage())
                .errors(alreadyExistsException.getErrors())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationExceptionResponse handleMethodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException) {
        var errors = new HashMap<String, String>();
        methodArgumentNotValidException.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ValidationExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(VALIDATION_FAILED_MESSAGE)
                .errors(errors)
                .build();
    }


}
