package com.modsen.rideservice.client;

import com.modsen.rideservice.dto.response.DriverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(value = "driver")
public interface DriverFeignClient {
    @GetMapping("/{id}")
    DriverResponse getDriver(@PathVariable("id") long id);
}
