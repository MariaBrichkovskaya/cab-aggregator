package com.modsen.rideservice.service;


import com.modsen.rideservice.dto.request.CreateRideRequest;
import com.modsen.rideservice.dto.request.DriverForRideRequest;
import com.modsen.rideservice.dto.request.StatusRequest;
import com.modsen.rideservice.dto.request.UpdateRideRequest;
import com.modsen.rideservice.dto.response.MessageResponse;
import com.modsen.rideservice.dto.response.RideResponse;
import com.modsen.rideservice.dto.response.RidesListResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.UUID;

public interface RideService {
    RideResponse add(CreateRideRequest request, OAuth2User principal);

    void sendEditStatus(DriverForRideRequest request);

    RideResponse findById(Long id);

    RidesListResponse findAll(int page, int size, String sortingParam);

    RideResponse update(UpdateRideRequest request, Long id);

    MessageResponse delete(Long id);

    RidesListResponse getRidesByPassengerId(UUID passengerId, int page, int size, String orderBy);

    RidesListResponse getRidesByDriverId(UUID driverId, int page, int size, String orderBy);

    RideResponse editStatus(long id, StatusRequest statusRequest);
}
