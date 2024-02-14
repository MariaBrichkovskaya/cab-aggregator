package com.modsen.authservice.exception;


import com.modsen.authservice.dto.response.ExceptionResponse;
import com.modsen.authservice.dto.response.ValidationExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class AuthHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationExceptionResponse handleMethodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException) {
        String VALIDATION_FAILED_MESSAGE = "Invalid request";
        Map<String, String> errors = methodArgumentNotValidException.getBindingResult().getAllErrors().stream()
                .map(error -> (FieldError) error)
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        return new ValidationExceptionResponse(HttpStatus.BAD_REQUEST, VALIDATION_FAILED_MESSAGE, errors);
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

