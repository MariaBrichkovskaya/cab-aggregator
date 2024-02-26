package com.modsen.passengerservice.service;

import com.modsen.passengerservice.dto.response.DriverResponse;

import java.util.UUID;

public interface DriverService {
    DriverResponse getDriver(UUID id);
}

