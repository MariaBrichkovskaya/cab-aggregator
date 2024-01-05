package com.modsen.rideservice.service;


import com.modsen.rideservice.dto.request.CreateRideRequest;
import com.modsen.rideservice.dto.request.DriverForRideRequest;
import com.modsen.rideservice.dto.request.StatusRequest;
import com.modsen.rideservice.dto.request.UpdateRideRequest;
import com.modsen.rideservice.dto.response.MessageResponse;
import com.modsen.rideservice.dto.response.RideResponse;
import com.modsen.rideservice.dto.response.RidesListResponse;

public interface RideService {
    RideResponse add(CreateRideRequest request);

    void sendEditStatus(DriverForRideRequest request);

    RideResponse findById(Long id);

    RidesListResponse findAll(int page, int size, String sortingParam);

    RideResponse update(UpdateRideRequest request, Long id);

    void delete(Long id);

    RidesListResponse getRidesByPassengerId(long passengerId, int page, int size, String orderBy);

    RidesListResponse getRidesByDriverId(long driverId, int page, int size, String orderBy);

    MessageResponse editStatus(long id, StatusRequest statusRequest);
}
