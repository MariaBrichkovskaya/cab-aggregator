package com.modsen.rideservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Builder
public record DriverForRideRequest(
        UUID driverId,
        long rideId
) {
}
