package com.modsen.driverservice.service;

import com.modsen.driverservice.dto.response.PassengerResponse;

import java.util.UUID;

public interface PassengerService {
    PassengerResponse getPassenger(UUID id);
}
