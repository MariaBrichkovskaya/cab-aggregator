package com.modsen.passengerservice.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MessageResponse {
    String message;
}
