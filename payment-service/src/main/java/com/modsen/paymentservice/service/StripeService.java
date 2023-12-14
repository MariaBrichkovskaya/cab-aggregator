package com.modsen.paymentservice.service;

import com.modsen.paymentservice.dto.request.CardRequest;
import com.modsen.paymentservice.dto.request.ChargeRequest;
import com.modsen.paymentservice.dto.request.CustomerChargeRequest;
import com.modsen.paymentservice.dto.request.CustomerRequest;
import com.modsen.paymentservice.dto.response.BalanceResponse;
import com.modsen.paymentservice.dto.response.MessageResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.Balance;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;

public interface StripeService {
    String charge(ChargeRequest request) throws StripeException;

    String create(CardRequest request) throws StripeException;

    Customer createCustomer(CustomerRequest request) throws StripeException;

    Customer retrieve(String id) throws StripeException;

    BalanceResponse balance() throws StripeException;


    MessageResponse chargeFromCustomer(CustomerChargeRequest request) throws StripeException;
}
