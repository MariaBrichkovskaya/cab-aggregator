package com.modsen.rideservice.dto.request;

import lombok.Builder;

import java.util.UUID;

@Builder
public record EditDriverStatusRequest(
        UUID driverId
) {
}