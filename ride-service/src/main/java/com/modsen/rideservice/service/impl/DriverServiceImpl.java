package com.modsen.rideservice.service.impl;

import com.modsen.rideservice.client.DriverFeignClient;
import com.modsen.rideservice.dto.response.DriverResponse;
import com.modsen.rideservice.service.DriverService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService {
    private final DriverFeignClient driverFeignClient;

    @CircuitBreaker(name = "breaker", fallbackMethod = "getFallbackDriver")
    @Retry(name = "driverProxyRetry")
    public DriverResponse getDriver(long id) {
        return driverFeignClient.getDriver(id);
    }

    private DriverResponse getFallbackDriver(long id, Exception exception) {
        log.error(exception.getMessage());
        return DriverResponse.builder()
                .id(id)
                .name("default")
                .surname("default")
                .phone("default")
                .active(false)
                .rating(5.0)
                .build();
    }
}