package com.modsen.rideservice.service.impl;

import com.modsen.rideservice.client.DriverFeignClient;
import com.modsen.rideservice.dto.response.DriverResponse;
import com.modsen.rideservice.service.DriverService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.modsen.rideservice.util.Messages.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService {
    private final DriverFeignClient driverFeignClient;

    @CircuitBreaker(name = "driverBreaker", fallbackMethod = "getFallbackDriver")
    @Retry(name = "driverProxyRetry")
    public DriverResponse getDriver(long id) {
        return driverFeignClient.getDriver(id);
    }

    private DriverResponse getFallbackDriver(long id, Exception exception) {
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
