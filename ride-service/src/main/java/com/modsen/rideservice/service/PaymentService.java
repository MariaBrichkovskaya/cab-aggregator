package com.modsen.rideservice.service;

import com.modsen.rideservice.dto.request.CustomerChargeRequest;
import com.modsen.rideservice.dto.request.CustomerRequest;
import com.modsen.rideservice.dto.response.ChargeResponse;
import com.modsen.rideservice.dto.response.ExistenceResponse;

public interface PaymentService {


    ChargeResponse chargeFromCustomer(CustomerChargeRequest request);

    void createCustomer(CustomerRequest request);

    ExistenceResponse customerExistence(long id);
}
