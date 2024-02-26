package com.modsen.driverservice.dto.request;

import lombok.Builder;

import java.util.UUID;

@Builder
public record EditDriverStatusRequest(
        UUID driverId
) {
}