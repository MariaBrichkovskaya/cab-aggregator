package com.modsen.driverservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.util.Map;
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Setter
public class ValidationExceptionResponse{
        final HttpStatus status;
        final String message;
        final Map<String, String> errors;
}