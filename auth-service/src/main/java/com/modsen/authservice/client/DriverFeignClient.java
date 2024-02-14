package com.modsen.authservice.client;

import com.modsen.authservice.config.FeignClientConfig;
import com.modsen.authservice.dto.request.DriverRequest;
import com.modsen.authservice.dto.response.DriverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "${feign.client.config.driver.name}",
        path = "${feign.client.config.driver.path}", configuration = FeignClientConfig.class)
public interface DriverFeignClient {
    @PostMapping
    DriverResponse add(DriverRequest request);
}