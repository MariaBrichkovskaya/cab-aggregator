package com.modsen.driverservice.service;

import com.modsen.driverservice.dto.request.DriverRequest;
import com.modsen.driverservice.dto.request.RideRequest;
import com.modsen.driverservice.dto.response.DriverResponse;
import com.modsen.driverservice.dto.response.DriversListResponse;
import com.modsen.driverservice.dto.response.MessageResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.UUID;

public interface DriverService {
    DriverResponse add(OAuth2User principal);

    DriverResponse findById(UUID id);

    DriversListResponse findAll(int page, int size, String sortingParam);

    DriverResponse update(DriverRequest request, UUID id);

    MessageResponse delete(UUID id);

    MessageResponse changeStatus(UUID id);

    DriversListResponse findAvailableDrivers(int page, int size, String sortingParam);

    void findDriverForRide(RideRequest request);
}
