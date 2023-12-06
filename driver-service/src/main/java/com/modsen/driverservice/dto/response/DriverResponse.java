package com.modsen.driverservice.dto.response;

import com.modsen.driverservice.enums.Status;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverResponse {
    Long id;
    String name;
    String surname;
    String phone;
    Double rating;
    Status status;
}
