package com.modsen.passengerservice.client;

import com.modsen.passengerservice.config.FeignClientConfig;
import com.modsen.passengerservice.dto.response.DriverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(value = "${feign.client.config.driver.name}"
        , configuration = FeignClientConfig.class, path = "${feign.client.config.driver.path}")
public interface DriverFeignClient {
    @GetMapping("/{id}")
    DriverResponse getDriver(@PathVariable("id") long id);

}
