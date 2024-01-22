package com.modsen.passengerservice.service;

import com.modsen.passengerservice.dto.response.DriverResponse;

public interface DriverService {
    DriverResponse getDriver(long id);
}

