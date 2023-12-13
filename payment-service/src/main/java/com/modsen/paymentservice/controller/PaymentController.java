package com.modsen.paymentservice.controller;


import com.modsen.paymentservice.service.StripeService;
import com.modsen.paymentservice.utils.ChargeRequest;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Token;
import com.stripe.net.RequestOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private StripeService stripeService;
    @Value ("${stripe.key.public}")
    private String PUBLIC_KEY;


    @PostMapping("/charge")
    public ResponseEntity<String> chargeCard(@RequestBody ChargeRequest chargeRequest) {
        try {
            String paymentId = stripeService.charge(chargeRequest.getAmount(), chargeRequest.getCurrency(), chargeRequest.getCardToken());
            return ResponseEntity.ok("Payment successful. ID: " + paymentId);
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment failed: " + e.getMessage());
        }
    }
    // Метод для создания токена
    @PostMapping("/create")
    public String createToken(String cardNumber, int expMonth, int expYear, String cvc) {
        Stripe.apiKey = PUBLIC_KEY;
        try {
            Map<String, Object> card = new HashMap<>();
            card.put("number", cardNumber);
            card.put("exp_month", expMonth);
            card.put("exp_year", expYear);
            card.put("cvc", cvc);
            Map<String, Object> params = new HashMap<>();
            params.put("card", card);
            Token token = Token.create(params);
            return token.getId();
        } catch (StripeException e) {
            // Обработка ошибки при создании токена
            e.printStackTrace();
            return null;
        }
    }


}