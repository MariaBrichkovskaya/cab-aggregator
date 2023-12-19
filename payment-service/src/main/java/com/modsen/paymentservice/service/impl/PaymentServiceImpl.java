package com.modsen.paymentservice.service.impl;

import com.modsen.paymentservice.enums.PaymentMethodEnum;
import com.modsen.paymentservice.dto.request.CardRequest;
import com.modsen.paymentservice.dto.request.ChargeRequest;
import com.modsen.paymentservice.dto.request.CustomerChargeRequest;
import com.modsen.paymentservice.dto.request.CustomerRequest;
import com.modsen.paymentservice.dto.response.*;
import com.modsen.paymentservice.exception.AlreadyExistsException;
import com.modsen.paymentservice.exception.BalanceException;
import com.modsen.paymentservice.exception.NotFoundException;
import com.modsen.paymentservice.model.User;
import com.modsen.paymentservice.repository.CustomerRepository;
import com.modsen.paymentservice.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentIntentConfirmParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${stripe.key.secret}")
    private String SECRET_KEY;
    @Value("${stripe.key.public}")
    private String PUBLIC_KEY;
    private final CustomerRepository customerRepository;

    @Override
    public MessageResponse charge(ChargeRequest request) throws StripeException {
        Stripe.apiKey = SECRET_KEY;
        Charge charge = Charge.create(
                Map.of(
                        "amount", request.getAmount() * 100,
                        "currency", request.getCurrency(),
                        "source", request.getCardToken()
                )
        );
        String message = "Payment successful. ID: " + charge.getId();
        return MessageResponse.builder().message(message).build();
    }


    @Override
    public TokenResponse create(CardRequest request) throws StripeException {
        Stripe.apiKey = PUBLIC_KEY;
        Map<String, Object> card = Map.of(
                "number", request.getCardNumber(),
                "exp_month", request.getExpMonth(),
                "exp_year", request.getExpYear(),
                "cvc", request.getCvc()
        );
        Token token = Token.create(Map.of("card", card));
        return TokenResponse.builder().token(token.getId()).build();
    }


    @Override
    public CustomerResponse createCustomer(CustomerRequest request) throws StripeException {
        Stripe.apiKey = PUBLIC_KEY;
        checkCustomerExistence(request.getPassengerId());
        CustomerCreateParams params =
                CustomerCreateParams.builder()
                        .setName(request.getName())
                        .setEmail(request.getEmail())
                        .setPhone(request.getPhone())
                        .setBalance(request.getAmount())
                        .build();
        Stripe.apiKey = SECRET_KEY;
        return createUser(params, request.getPassengerId());
    }

    private void checkCustomerNotFound(long id) {
        if (!customerRepository.existsById(id))
            throw new NotFoundException("Customer is not found");
    }

    private void checkCustomerExistence(long id) {
        if (!customerRepository.existsById(id))
            throw new AlreadyExistsException("Customer already exists");
    }

    private CustomerResponse createUser(CustomerCreateParams params, long id) throws StripeException {
        Stripe.apiKey = SECRET_KEY;
        Customer customer = Customer.create(params);
        createPaymentMethod(customer.getId());
        User user = User
                .builder().customerId(customer.getId())
                .passengerId(id).build();
        customerRepository.save(user);
        return CustomerResponse.builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .name(customer.getName()).build();
    }

    private void createPaymentMethod(String customerId) throws StripeException {
        Stripe.apiKey = SECRET_KEY;
        Map<String, Object> paymentMethodParams = Map.of(
                "type", "card",
                "card", Map.of("token", "tok_visa")
        );
        PaymentMethod paymentMethod = PaymentMethod.create(paymentMethodParams);
        paymentMethod.attach(Map.of("customer", customerId));
    }


    @Override
    public CustomerResponse retrieve(long id) throws StripeException {
        Stripe.apiKey = SECRET_KEY;
        checkCustomerNotFound(id);
        String customerId = customerRepository.findById(id).get().getCustomerId();
        Customer customer = Customer.retrieve(customerId);
        return CustomerResponse.builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .name(customer.getName()).build();
    }

    @Override
    public BalanceResponse balance() throws StripeException {
        Stripe.apiKey = SECRET_KEY;
        Balance balance = Balance.retrieve();
        return BalanceResponse
                .builder()
                .amount(balance.getPending().get(0).getAmount())
                .currency(balance.getPending().get(0).getCurrency())
                .build();
    }

    @Override
    public ChargeResponse chargeFromCustomer(CustomerChargeRequest request) throws StripeException {
        Stripe.apiKey = SECRET_KEY;
        Long passengerId = request.getPassengerId();
        User user = customerRepository.findById(passengerId).get();
        String customerId = user.getCustomerId();
        checkBalance(customerId, request.getAmount() * 100);
        PaymentIntent intent = createIntent(request, customerId);
        updateBalance(customerId, request.getAmount());
        return ChargeResponse.builder().id(intent.getId())
                .amount(intent.getAmount() / 100)
                .currency(intent.getCurrency()).build();
    }

    private PaymentIntent createIntent(CustomerChargeRequest request, String customerId) throws StripeException {
        PaymentIntent intent = PaymentIntent.create(Map.of("amount", request.getAmount() * 100,
                "currency", request.getCurrency(),
                "customer", customerId));
        intent.setPaymentMethod(customerId);
        PaymentIntentConfirmParams params =
                PaymentIntentConfirmParams.builder()
                        .setPaymentMethod(PaymentMethodEnum.VISA.getMethod())
                        .build();
        return intent.confirm(params);
    }

    private void updateBalance(String customerId, long amount) throws StripeException {
        Customer customer = Customer.retrieve(customerId);
        CustomerUpdateParams params =
                CustomerUpdateParams.builder()
                        .setBalance(customer.getBalance() - amount * 100)
                        .build();
        Stripe.apiKey = SECRET_KEY;
        customer.update(params);
    }

    private void checkBalance(String customerId, long amount) throws StripeException {
        Customer customer = Customer.retrieve(customerId);
        Long balance = customer.getBalance();
        if (balance < amount)
            throw new BalanceException("Not enough money in the account");

    }

}

