package com.modsen.rideservice.service;

import com.modsen.rideservice.dto.request.CustomerChargeRequest;
import com.modsen.rideservice.dto.request.CustomerRequest;
import com.modsen.rideservice.dto.response.ChargeResponse;
import com.modsen.rideservice.dto.response.CustomerResponse;

public interface PaymentService {
    CustomerResponse findCustomer(long id);


    ChargeResponse chargeFromCustomer(CustomerChargeRequest request);

    CustomerResponse createCustomer(CustomerRequest request);
}
