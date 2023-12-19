package com.modsen.rideservice.client;

import com.modsen.rideservice.dto.response.DriverResponse;
import com.modsen.rideservice.dto.response.DriversListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "driver")
public interface DriverFeignClient {
    @GetMapping("/{id}")
    DriverResponse getDriver(@PathVariable("id") long id);

    @GetMapping("/available")
    DriversListResponse getAvailable(@RequestParam(required = false, defaultValue = "1") int page,
                                     @RequestParam(required = false, defaultValue = "10") int size,
                                     @RequestParam(name = "order_by", required = false) String orderBy);

    @PutMapping("/{id}/status")
    void changeStatus(@PathVariable Long id);

}

