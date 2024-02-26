package com.modsen.rideservice.dto.response;

import com.modsen.rideservice.enums.DriverStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverResponse {
    UUID id;
    String name;
    String surname;
    String phone;
    Double rating;
    DriverStatus status;
    boolean active;
}
