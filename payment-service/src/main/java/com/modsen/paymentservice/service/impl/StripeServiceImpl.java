package com.modsen.paymentservice.service.impl;

import com.modsen.paymentservice.dto.request.CardRequest;
import com.modsen.paymentservice.dto.request.ChargeRequest;
import com.modsen.paymentservice.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.key.secret}")
    private String SECRET_KEY;
    @Value("${stripe.key.public}")
    private String PUBLIC_KEY;

    @Override
    public String charge(ChargeRequest request) throws StripeException {
        Stripe.apiKey = SECRET_KEY;

        Map<String, Object> params = new HashMap<>();
        params.put("amount", request.getAmount());
        params.put("currency", request.getCurrency());
        params.put("source", request.getCardToken());

        Charge charge = Charge.create(params);

        return charge.getId();
    }

    @Override
    public String create(CardRequest request) throws StripeException {
        Stripe.apiKey = PUBLIC_KEY;
        Map<String, Object> card = new HashMap<>();
        card.put("number", request.getCardNumber());
        card.put("exp_month", request.getExpMonth());
        card.put("exp_year", request.getExpYear());
        card.put("cvc", request.getCvc());
        Map<String, Object> params = new HashMap<>();
        params.put("card", card);
        Token token = Token.create(params);
        return token.getId();
    }

}
