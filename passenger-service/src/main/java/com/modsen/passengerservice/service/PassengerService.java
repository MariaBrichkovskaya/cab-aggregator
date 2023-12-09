package com.modsen.passengerservice.service;

import com.modsen.passengerservice.dto.request.PassengerRequest;
import com.modsen.passengerservice.dto.request.PassengerRatingRequest;
import com.modsen.passengerservice.dto.response.PassengerResponse;
import com.modsen.passengerservice.dto.response.PassengersListResponse;

public interface PassengerService {
    void add(PassengerRequest request);
    PassengerResponse findById(Long id);
    PassengersListResponse findAll(int page, int size, String sortingParam);
    void update(PassengerRequest request,Long id);
    void delete(Long id);
}
