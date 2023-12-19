package com.modsen.rideservice.client;

import com.modsen.rideservice.config.FeignClientConfig;
import com.modsen.rideservice.dto.request.CustomerChargeRequest;
import com.modsen.rideservice.dto.request.CustomerRequest;
import com.modsen.rideservice.dto.response.ChargeResponse;
import com.modsen.rideservice.dto.response.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
@FeignClient(value = "payment",configuration = FeignClientConfig.class)
public interface PaymentFeignClient {
    @GetMapping("/customers/{id}")
    CustomerResponse findCustomer(@PathVariable long id);
    @PostMapping("/customers/charge")
    ChargeResponse chargeFromCustomer(@RequestBody CustomerChargeRequest request);
    @PostMapping("/customers")
    CustomerResponse createCustomer(@RequestBody CustomerRequest request);
}
