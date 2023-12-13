package com.modsen.paymentservice.service;

import com.modsen.paymentservice.dto.request.CardRequest;
import com.modsen.paymentservice.dto.request.ChargeRequest;
import com.stripe.exception.StripeException;

public interface StripeService {
    String charge(ChargeRequest request) throws StripeException;

    String create(CardRequest request) throws StripeException;
}
