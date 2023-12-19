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
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @PostMapping("/charge")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse chargeCard(@RequestBody @Valid ChargeRequest chargeRequest) throws StripeException {
        return paymentService.charge(chargeRequest);
    }

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse createToken(@RequestBody @Valid CardRequest request) throws StripeException {
        return paymentService.create(request);
    }

    @PostMapping("/customers")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse createCustomer(@RequestBody @Valid CustomerRequest request) throws StripeException {
        return paymentService.createCustomer(request);
    }

    @GetMapping("/customers/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerResponse findCustomer(@PathVariable long id) throws StripeException {
        return paymentService.retrieve(id);
    }

    @GetMapping("/balance")
    @ResponseStatus(HttpStatus.OK)
    public BalanceResponse balance() throws StripeException {
        return paymentService.balance();
    }

    @PostMapping("/customers/charge")
    @ResponseStatus(HttpStatus.OK)
    public ChargeResponse chargeFromCustomer(@RequestBody @Valid CustomerChargeRequest request) throws StripeException {
        return paymentService.chargeFromCustomer(request);
    }

}