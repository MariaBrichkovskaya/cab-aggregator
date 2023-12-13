package com.modsen.paymentservice.controller;


import com.modsen.paymentservice.dto.request.CardRequest;
import com.modsen.paymentservice.dto.response.MessageResponse;
import com.modsen.paymentservice.dto.request.ChargeRequest;
import com.modsen.paymentservice.service.StripeService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final StripeService stripeService;


    @PostMapping("/charge")
    public ResponseEntity<MessageResponse> chargeCard(@RequestBody ChargeRequest chargeRequest) throws StripeException {
        String paymentId = stripeService.charge(chargeRequest);
        String message = "Payment successful. ID: " + paymentId;
        return ResponseEntity.ok(MessageResponse.builder().message(message).build());
    }

    // Метод для создания токена
    @PostMapping("/create")
    public ResponseEntity<MessageResponse> createToken(@RequestBody CardRequest request) throws StripeException {
        String tokenId = stripeService.create(request);

        return ResponseEntity.ok(MessageResponse.builder().message(tokenId).build());
    }

}