package com.modsen.driverservice.dto.request;

import lombok.*;

@Builder
public record DriverForRideRequest(long driverId, long rideId) {
}

