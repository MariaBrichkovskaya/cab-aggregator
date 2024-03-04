package com.modsen.rideservice.service.impl;

import com.modsen.rideservice.client.PaymentFeignClient;
import com.modsen.rideservice.dto.request.CustomerChargeRequest;
import com.modsen.rideservice.dto.request.CustomerRequest;
import com.modsen.rideservice.dto.response.ChargeResponse;
import com.modsen.rideservice.dto.response.CustomerResponse;
import com.modsen.rideservice.dto.response.ExistenceResponse;
import com.modsen.rideservice.exception.PaymentFallbackException;
import com.modsen.rideservice.service.PaymentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@CircuitBreaker(name = "paymentBreaker", fallbackMethod = "fallbackException")
@Retry(name = "proxyRetry")
@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentFeignClient paymentFeignClient;


    @Override
    public ChargeResponse chargeFromCustomer(CustomerChargeRequest request) {
        return paymentFeignClient.chargeFromCustomer(request);
    }

    @Override
    public void createCustomer(CustomerRequest request) {
        paymentFeignClient.createCustomer(request);
    }

    @Override
    public ExistenceResponse customerExistence(UUID id) {
        return paymentFeignClient.customerExistence(id);
    }

    private ChargeResponse fallbackException(CustomerChargeRequest request, Exception exception) {
        log.error(exception.getMessage());
        throw new PaymentFallbackException(exception.getMessage());
    }

    private CustomerResponse fallbackException(CustomerRequest request, Exception exception) {
        log.error(exception.getMessage());
        throw new PaymentFallbackException(exception.getMessage());
    }

    private ExistenceResponse fallbackException(UUID id, Exception exception) {
        log.error(exception.getMessage());
        throw new PaymentFallbackException(exception.getMessage());
    }
}
