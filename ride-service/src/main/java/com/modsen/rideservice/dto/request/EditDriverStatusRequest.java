package com.modsen.rideservice.dto.request;

import lombok.Builder;

@Builder
public record EditDriverStatusRequest(
        long driverId
) {
}