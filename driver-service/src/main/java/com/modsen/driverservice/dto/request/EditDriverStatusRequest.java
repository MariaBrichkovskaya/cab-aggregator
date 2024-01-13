package com.modsen.driverservice.dto.request;

import lombok.Builder;

@Builder
public record EditDriverStatusRequest(
        long driverId
) {
}