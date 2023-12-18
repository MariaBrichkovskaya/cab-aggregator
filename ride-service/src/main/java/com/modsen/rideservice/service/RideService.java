package com.modsen.rideservice.service;


import com.modsen.rideservice.dto.request.CreateRideRequest;
import com.modsen.rideservice.dto.request.StatusRequest;
import com.modsen.rideservice.dto.request.UpdateRideRequest;
import com.modsen.rideservice.dto.response.RideResponse;
import com.modsen.rideservice.dto.response.RidesListResponse;

import java.util.concurrent.ExecutionException;

public interface RideService {
    RideResponse add(CreateRideRequest request) throws ExecutionException, InterruptedException;
    RideResponse findById(Long id);
    RidesListResponse findAll(int page, int size, String sortingParam);
    void update(UpdateRideRequest request, Long id);
    void delete(Long id);
    RidesListResponse getRidesByPassengerId(long passengerId, int page, int size, String orderBy);
    RidesListResponse getRidesByDriverId(long driverId, int page, int size, String orderBy);

    void editStatus(long id, StatusRequest statusRequest);
}
