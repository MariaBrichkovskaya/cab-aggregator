package com.modsen.rideservice.client;

import com.modsen.rideservice.dto.response.DriverResponse;
import com.modsen.rideservice.dto.response.PassengerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(value = "passenger",configuration = CustomErrorDecoder.class)
public interface PassengerFeignClient {
    @GetMapping("/{id}")
    PassengerResponse getPassenger(@PathVariable("id") long id);
}
