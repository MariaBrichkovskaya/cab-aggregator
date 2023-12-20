package com.modsen.driverservice.cient;

import com.modsen.driverservice.config.FeignClientConfig;
import com.modsen.driverservice.dto.response.PassengerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "passenger",configuration = FeignClientConfig.class)
public interface PassengerFeignClient {
    @GetMapping("/{id}")
    PassengerResponse getPassenger(@PathVariable long id);
}
