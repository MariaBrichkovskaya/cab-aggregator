package com.modsen.driverservice.service;

import com.modsen.driverservice.dto.request.DriverRequest;
import com.modsen.driverservice.dto.response.DriverResponse;
import com.modsen.driverservice.dto.response.DriversListResponse;

import java.util.List;

public interface DriverService {
    void add(DriverRequest request);
    DriverResponse findById(Long id);
    DriversListResponse findAll(int page, int size, String sortingParam);
    void update(DriverRequest request,Long id);
    void delete(Long id);
    void changeStatus(Long id);
    void editRating(Double score,Long id);
    DriversListResponse findAvailableDrivers();
}
