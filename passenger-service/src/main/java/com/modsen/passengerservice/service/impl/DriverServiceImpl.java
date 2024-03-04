package com.modsen.passengerservice.service.impl;

import com.modsen.passengerservice.client.DriverFeignClient;
import com.modsen.passengerservice.dto.response.DriverResponse;
import com.modsen.passengerservice.service.DriverService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService {
    private final DriverFeignClient driverFeignClient;
    private final String DEFAULT = "default";
    private final double DEFAULT_RATING = 5.0;

    @CircuitBreaker(name = "breaker", fallbackMethod = "getFallbackDriver")
    @Retry(name = "driverProxyRetry")
    public DriverResponse getDriver(UUID id) {
        return driverFeignClient.getDriver(id);
    }

    private DriverResponse getFallbackDriver(UUID id, Exception exception) {
        log.error(exception.getMessage());
        return DriverResponse.builder()
                .id(id)
                .name(DEFAULT)
                .surname(DEFAULT)
                .phone(DEFAULT)
                .rating(DEFAULT_RATING)
                .active(false)
                .build();
    }
}
