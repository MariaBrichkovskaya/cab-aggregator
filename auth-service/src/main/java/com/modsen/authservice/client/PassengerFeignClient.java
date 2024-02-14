package com.modsen.authservice.client;

import com.modsen.authservice.config.FeignClientConfig;
import com.modsen.authservice.dto.request.PassengerRequest;
import com.modsen.authservice.dto.response.PassengerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "${feign.client.config.passenger.name}",
        path = "${feign.client.config.passenger.path}", configuration = FeignClientConfig.class)
public interface PassengerFeignClient {
    @PostMapping
    PassengerResponse add(PassengerRequest request);
}
