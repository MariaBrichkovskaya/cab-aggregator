package com.modsen.paymentservice.service;

import com.modsen.paymentservice.dto.request.CardRequest;
import com.modsen.paymentservice.dto.request.ChargeRequest;
import com.modsen.paymentservice.dto.request.CustomerChargeRequest;
import com.modsen.paymentservice.dto.request.CustomerRequest;
import com.modsen.paymentservice.dto.response.*;

public interface PaymentService {
    MessageResponse charge(ChargeRequest request);

    TokenResponse create(CardRequest request);

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse retrieve(long id);

    BalanceResponse balance();

    ChargeResponse chargeFromCustomer(CustomerChargeRequest request);
}
