package com.modsen.rideservice.client;

import com.modsen.rideservice.config.FeignClientConfig;
import com.modsen.rideservice.dto.response.DriverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;


@FeignClient(value = "${feign.client.config.driver.name}",
        path = "${feign.client.config.driver.path}", configuration = FeignClientConfig.class)
public interface DriverFeignClient {
    @GetMapping("/{id}")
    DriverResponse getDriver(@PathVariable("id") UUID id);

}

