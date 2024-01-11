package com.modsen.passengerservice.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@ToString
@EqualsAndHashCode
public class ExceptionResponse {
    final HttpStatus status;
    final String message;
}
