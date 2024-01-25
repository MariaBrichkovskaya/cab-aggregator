package com.modsen.driverservice.service;

import com.modsen.driverservice.dto.response.PassengerResponse;

public interface PassengerService {
    PassengerResponse getPassenger(long id);
}
