package com.modsen.driverservice.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AlreadyExistsResponse {
    final HttpStatus status;
    final String message;
    final Map<String, String> errors;
}
