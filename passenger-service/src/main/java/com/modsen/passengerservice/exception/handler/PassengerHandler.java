package com.modsen.passengerservice.exception.handler;

import com.modsen.passengerservice.dto.response.ExceptionResponse;
import com.modsen.passengerservice.dto.response.ValidationExceptionResponse;
import com.modsen.passengerservice.exception.AlreadyExistsException;
import com.modsen.passengerservice.exception.InvalidRequestException;
import com.modsen.passengerservice.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

import static com.modsen.passengerservice.util.Messages.*;

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
    public ResponseEntity<Object> handleDriverAlreadyExists(AlreadyExistsException alreadyExistsException) {
        ExceptionResponse response=
                new ExceptionResponse( HttpStatus.CONFLICT,
                        alreadyExistsException.getMessage()
                );
        return new ResponseEntity<>(response,response.getStatus());
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
