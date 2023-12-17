package com.modsen.rideservice.dto.response;

import com.modsen.rideservice.enums.DriverStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverResponse {
    long id;
    String name;
    String surname;
    String phone;
    Double rating;
    DriverStatus status;
}
