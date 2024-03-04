package com.modsen.driverservice.dto.request;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DriverForRideRequest(
        UUID driverId,
        long rideId
) {
}

