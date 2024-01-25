package com.modsen.rideservice.service;

import com.modsen.rideservice.dto.response.PassengerResponse;

public interface PassengerService {
    PassengerResponse getPassenger(long id);
}
