package com.modsen.rideservice.service;


import com.modsen.rideservice.dto.request.RideRequest;
import com.modsen.rideservice.dto.request.StatusRequest;
import com.modsen.rideservice.dto.response.RideResponse;
import com.modsen.rideservice.dto.response.RidesListResponse;
import com.modsen.rideservice.enums.Status;
import org.springframework.transaction.annotation.Transactional;

public interface RideService {
    void add(RideRequest request);
    RideResponse findById(Long id);
    RidesListResponse findAll(int page, int size, String sortingParam);
    void update(RideRequest request,Long id);
    void delete(Long id);
    RidesListResponse getRidesByPassengerId(long passengerId, int page, int size, String orderBy);
    RidesListResponse getRidesByDriverId(long driverId, int page, int size, String orderBy);

    void editStatus(long id, StatusRequest statusRequest);
}
