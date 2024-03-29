package com.modsen.rideservice.dto.response;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Builder
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ValidationExceptionResponse {
    final HttpStatus status;
    final String message;
    final Map<String, String> errors;
}