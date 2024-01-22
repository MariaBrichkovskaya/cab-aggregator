package com.modsen.rideservice.service;

import com.modsen.rideservice.dto.response.DriverResponse;

public interface DriverService {
    DriverResponse getDriver(long id);
}
