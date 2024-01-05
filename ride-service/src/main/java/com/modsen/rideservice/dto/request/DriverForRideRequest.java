package com.modsen.rideservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
public record DriverForRideRequest(
        long driverId,
        long rideId
) {
}
