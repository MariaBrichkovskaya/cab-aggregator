package com.modsen.paymentservice.controller;


import com.modsen.paymentservice.dto.request.CardRequest;
import com.modsen.paymentservice.dto.request.CustomerChargeRequest;
import com.modsen.paymentservice.dto.request.CustomerRequest;
import com.modsen.paymentservice.dto.response.BalanceResponse;
import com.modsen.paymentservice.dto.response.CustomerResponse;
import com.modsen.paymentservice.dto.response.MessageResponse;
import com.modsen.paymentservice.dto.request.ChargeRequest;
import com.modsen.paymentservice.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final StripeService stripeService;


    @PostMapping("/charge")
    public ResponseEntity<MessageResponse> chargeCard(@RequestBody @Valid ChargeRequest chargeRequest) throws StripeException {
        String paymentId = stripeService.charge(chargeRequest);
        String message = "Payment successful. ID: " + paymentId;
        return ResponseEntity.ok(MessageResponse.builder().message(message).build());
    }

    // Метод для создания токена
    @PostMapping("/token")
    public ResponseEntity<MessageResponse> createToken(@RequestBody @Valid CardRequest request) throws StripeException {
        String tokenId = stripeService.create(request);

        return ResponseEntity.ok(MessageResponse.builder().message(tokenId).build());
    }

    @PostMapping("/customers")
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody @Valid CustomerRequest request) throws StripeException {
        Customer customer = stripeService.createCustomer(request);
        CustomerResponse response = CustomerResponse.builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .name(customer.getName()).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerResponse> findCustomer(@PathVariable String id) throws StripeException {
        Customer customer = stripeService.retrieve(id);
        CustomerResponse response = CustomerResponse.builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .name(customer.getName()).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> balance() throws StripeException {
        BalanceResponse balance = stripeService.balance();
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/customers/charge")
    public ResponseEntity<MessageResponse> chargeFromCustomer(@RequestBody @Valid CustomerChargeRequest request) throws StripeException {
        return ResponseEntity.ok(stripeService.chargeFromCustomer(request));
    }

}