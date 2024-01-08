package com.modsen.rideservice.dto.request;

import lombok.*;

@Builder
public record EditDriverStatusRequest(
        long driverId
) {
}