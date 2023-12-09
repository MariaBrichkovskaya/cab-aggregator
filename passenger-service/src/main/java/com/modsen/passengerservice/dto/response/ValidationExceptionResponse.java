package com.modsen.passengerservice.dto.response;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.util.Map;
@Builder
public record ValidationExceptionResponse(
        HttpStatus status,
        String message,
        Map<String, String> errors
) {
}