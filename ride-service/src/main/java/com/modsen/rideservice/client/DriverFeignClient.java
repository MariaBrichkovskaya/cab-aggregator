package com.modsen.rideservice.client;

import com.modsen.rideservice.config.FeignClientConfig;
import com.modsen.rideservice.dto.response.DriverResponse;
import com.modsen.rideservice.dto.response.DriversListResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "${feign.client.config.driver.name}", url = "${feign.client.config.driver.url}",
        path = "${feign.client.config.driver.path}", configuration = FeignClientConfig.class)
public interface DriverFeignClient {
    @GetMapping("/{id}")
    DriverResponse getDriver(@PathVariable("id") long id);

}

