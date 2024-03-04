package com.modsen.rideservice.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@ToString
public class DriverRequest {
    UUID driverId;
}
