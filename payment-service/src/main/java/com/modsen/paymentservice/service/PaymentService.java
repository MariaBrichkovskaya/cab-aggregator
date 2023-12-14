package com.modsen.paymentservice.service;

import com.modsen.paymentservice.dto.request.CardRequest;
import com.modsen.paymentservice.dto.request.ChargeRequest;
import com.modsen.paymentservice.dto.request.CustomerChargeRequest;
import com.modsen.paymentservice.dto.request.CustomerRequest;
import com.modsen.paymentservice.dto.response.*;
import com.stripe.exception.StripeException;

public interface PaymentService {
    MessageResponse charge(ChargeRequest request) throws StripeException;

    TokenResponse create(CardRequest request) throws StripeException;

    CustomerResponse createCustomer(CustomerRequest request) throws StripeException;

    CustomerResponse retrieve(String id) throws StripeException;

    BalanceResponse balance() throws StripeException;


    ChargeResponse chargeFromCustomer(CustomerChargeRequest request) throws StripeException;
}
