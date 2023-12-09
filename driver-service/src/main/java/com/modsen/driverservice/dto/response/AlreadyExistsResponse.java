package com.modsen.driverservice.dto.response;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Builder
public record AlreadyExistsResponse(
        HttpStatus status,
        String message,
        Map<String, String> errors
) {
}