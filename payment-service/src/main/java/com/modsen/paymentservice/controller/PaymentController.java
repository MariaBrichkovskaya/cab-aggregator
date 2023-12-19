package com.modsen.paymentservice.controller;


import com.modsen.paymentservice.dto.request.CardRequest;
import com.modsen.paymentservice.dto.request.CustomerChargeRequest;
import com.modsen.paymentservice.dto.request.CustomerRequest;
import com.modsen.paymentservice.dto.response.*;
import com.modsen.paymentservice.dto.request.ChargeRequest;
import com.modsen.paymentservice.service.PaymentService;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @PostMapping("/charge")
    public ResponseEntity<MessageResponse> chargeCard(@RequestBody @Valid ChargeRequest chargeRequest) throws StripeException {
        return ResponseEntity.ok(paymentService.charge(chargeRequest));
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> createToken(@RequestBody @Valid CardRequest request) throws StripeException {
        return ResponseEntity.ok(paymentService.create(request));
    }

    @PostMapping("/customers")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody @Valid CustomerRequest request) throws StripeException {
        return ResponseEntity.ok(paymentService.createCustomer(request));
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerResponse> findCustomer(@PathVariable long id) throws StripeException {
        return ResponseEntity.ok(paymentService.retrieve(id));
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> balance() throws StripeException {
        BalanceResponse balance = paymentService.balance();
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/customers/charge")
    public ResponseEntity<ChargeResponse> chargeFromCustomer(@RequestBody @Valid CustomerChargeRequest request) throws StripeException {
        return ResponseEntity.ok(paymentService.chargeFromCustomer(request));
    }

}