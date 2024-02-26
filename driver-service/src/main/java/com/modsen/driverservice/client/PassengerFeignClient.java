package com.modsen.driverservice.client;

import com.modsen.driverservice.config.FeignClientConfig;
import com.modsen.driverservice.dto.response.PassengerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(value = "${feign.client.config.passenger.name}",
        path = "${feign.client.config.passenger.path}", configuration = FeignClientConfig.class)
public interface PassengerFeignClient {
    @GetMapping("/{id}")
    PassengerResponse getPassenger(@PathVariable UUID id);
}
