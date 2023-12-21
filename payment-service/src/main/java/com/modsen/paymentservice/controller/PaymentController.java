package com.modsen.paymentservice.controller;


import com.modsen.paymentservice.dto.request.CardRequest;
import com.modsen.paymentservice.dto.request.CustomerChargeRequest;
import com.modsen.paymentservice.dto.request.CustomerRequest;
import com.modsen.paymentservice.dto.response.*;
import com.modsen.paymentservice.dto.request.ChargeRequest;
import com.modsen.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @PostMapping("/charge")
    public MessageResponse chargeCard(@RequestBody @Valid ChargeRequest chargeRequest) {
        return paymentService.charge(chargeRequest);
    }

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse createToken(@RequestBody @Valid CardRequest request) {
        return paymentService.createTokent(request);
    }

    @PostMapping("/customers")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse createCustomer(@RequestBody @Valid CustomerRequest request) {
        return paymentService.createCustomer(request);
    }

    @GetMapping("/customers/{id}")
    public CustomerResponse findCustomer(@PathVariable long id) {
        return paymentService.retrieve(id);
    }

    @GetMapping("/balance")
    public BalanceResponse balance() {
        return paymentService.balance();
    }

    @PostMapping("/customers/charge")
    public ChargeResponse chargeFromCustomer(@RequestBody @Valid CustomerChargeRequest request) {
        return paymentService.chargeFromCustomer(request);
    }

}