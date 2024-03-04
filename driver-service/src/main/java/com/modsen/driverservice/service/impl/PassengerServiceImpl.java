package com.modsen.driverservice.service.impl;

import com.modsen.driverservice.client.PassengerFeignClient;
import com.modsen.driverservice.dto.response.PassengerResponse;
import com.modsen.driverservice.service.PassengerService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerServiceImpl implements PassengerService {
    private final PassengerFeignClient passengerFeignClient;
    private final String DEFAULT = "default";
    private final double DEFAULT_RATING = 5.0;

    @CircuitBreaker(name = "breaker", fallbackMethod = "getFallbackPassenger")
    @Retry(name = "proxyRetry")
    @Override
    public PassengerResponse getPassenger(UUID id) {
        return passengerFeignClient.getPassenger(id);
    }

    private PassengerResponse getFallbackPassenger(UUID id, Exception exception) {
        log.error(exception.getMessage());
        return PassengerResponse.builder()
                .id(id)
                .name(DEFAULT)
                .surname(DEFAULT)
                .email(DEFAULT)
                .phone(DEFAULT)
                .rating(DEFAULT_RATING)
                .active(false)
                .build();
    }
}
