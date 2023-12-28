package com.modsen.driverservice.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class DriverForRideRequest {
    long driverId;
    long rideId;
}
