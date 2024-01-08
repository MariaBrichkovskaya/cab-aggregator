package com.modsen.passengerservice.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverResponse {
    Long id;
    String name;
    String surname;
    String phone;
    Double rating;
    String status;
}
