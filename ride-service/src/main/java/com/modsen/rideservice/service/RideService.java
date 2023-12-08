package com.modsen.rideservice.service;


import com.modsen.rideservice.dto.request.RideRequest;
import com.modsen.rideservice.dto.response.RideResponse;
import com.modsen.rideservice.dto.response.RidesListResponse;

public interface RideService {
    void add(RideRequest request);
    RideResponse findById(Long id);
    RidesListResponse findAll(int page, int size, String sortingParam);
    void update(RideRequest request,Long id);
    void delete(Long id);
}
