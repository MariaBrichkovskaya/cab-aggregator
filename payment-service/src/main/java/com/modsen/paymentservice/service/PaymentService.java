package com.modsen.paymentservice.service;

import com.modsen.paymentservice.dto.request.CardRequest;
import com.modsen.paymentservice.dto.request.ChargeRequest;
import com.modsen.paymentservice.dto.request.CustomerChargeRequest;
import com.modsen.paymentservice.dto.request.CustomerRequest;
import com.modsen.paymentservice.dto.response.BalanceResponse;
import com.modsen.paymentservice.dto.response.ChargeResponse;
import com.modsen.paymentservice.dto.response.CustomerResponse;
import com.modsen.paymentservice.dto.response.ExistenceResponse;
import com.modsen.paymentservice.dto.response.MessageResponse;
import com.modsen.paymentservice.dto.response.TokenResponse;

public interface PaymentService {
    MessageResponse charge(ChargeRequest request);

    TokenResponse createTokent(CardRequest request);

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse retrieve(long id);

    BalanceResponse balance();

    ChargeResponse chargeFromCustomer(CustomerChargeRequest request);

    ExistenceResponse checkExistence(long id);
}
