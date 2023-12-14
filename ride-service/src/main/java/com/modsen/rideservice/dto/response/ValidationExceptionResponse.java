package com.modsen.rideservice.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.util.Map;
@Builder
@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ValidationExceptionResponse{
        final HttpStatus status;
        final String message;
        final Map<String, String> errors;
}