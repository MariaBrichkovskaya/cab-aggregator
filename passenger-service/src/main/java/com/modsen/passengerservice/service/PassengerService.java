package com.modsen.passengerservice.service;

import com.modsen.passengerservice.dto.request.PassengerRequest;
import com.modsen.passengerservice.dto.response.PassengerResponse;

import java.util.List;

public interface PassengerService {
    void add(PassengerRequest request);
    PassengerResponse findById(Long id);
    List<PassengerResponse> findAll();
    void update(PassengerRequest request,Long id);
    void delete(Long id);
}
