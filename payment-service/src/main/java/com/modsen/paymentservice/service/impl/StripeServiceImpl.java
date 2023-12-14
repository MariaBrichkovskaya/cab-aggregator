package com.modsen.paymentservice.service.impl;

import com.modsen.paymentservice.dto.request.CardRequest;
import com.modsen.paymentservice.dto.request.ChargeRequest;
import com.modsen.paymentservice.dto.request.CustomerChargeRequest;
import com.modsen.paymentservice.dto.request.CustomerRequest;
import com.modsen.paymentservice.dto.response.BalanceResponse;
import com.modsen.paymentservice.dto.response.MessageResponse;
import com.modsen.paymentservice.exception.AlreadyExistsException;
import com.modsen.paymentservice.model.User;
import com.modsen.paymentservice.repository.CustomerRepository;
import com.modsen.paymentservice.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentConfirmParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.key.secret}")
    private String SECRET_KEY;
    @Value("${stripe.key.public}")
    private String PUBLIC_KEY;
    private final CustomerRepository customerRepository;

    @Override
    public String charge(ChargeRequest request) throws StripeException {
        Stripe.apiKey = SECRET_KEY;

        Map<String, Object> params = new HashMap<>();
        params.put("amount", request.getAmount() * 100);
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

    @Override
    public Customer createCustomer(CustomerRequest request) throws StripeException {
        Stripe.apiKey = PUBLIC_KEY;
        if(customerRepository.existsById(request.getPassengerId()))
            throw new AlreadyExistsException("Customer with id "+request.getPassengerId()+" already exists");
        CustomerCreateParams params =
                CustomerCreateParams.builder()
                        .setName(request.getName())
                        .setEmail(request.getEmail())
                        .setPhone(request.getPhone())
                        .setBalance(request.getAmount())
                        .build();
        Stripe.apiKey = SECRET_KEY;
        Customer stripeCustomer = Customer.create(params);
        createPaymentMethod(stripeCustomer.getId());
        User user = User
                .builder().customerId(stripeCustomer.getId())
                .passengerId(request.getPassengerId()).build();
        customerRepository.save(user);
        return stripeCustomer;
    }

    private void createPaymentMethod(String customerId) throws StripeException {
        Stripe.apiKey = SECRET_KEY;
        Map<String, Object> paymentMethodParams = new HashMap<>();
        paymentMethodParams.put("type", "card");
        Map<String, Object> cardParams = new HashMap<>();
        cardParams.put("token", "tok_visa"); // здесь "tok_visa" - это пример токена тестирования для карты Visa
        paymentMethodParams.put("card", cardParams);

        PaymentMethod paymentMethod = PaymentMethod.create(paymentMethodParams);
        Map<String, Object> attachParams = new HashMap<>();
        attachParams.put("customer", customerId);

        paymentMethod.attach(attachParams);

    }

    @Override
    public Customer retrieve(String id) throws StripeException {
        Stripe.apiKey = SECRET_KEY;
        return Customer.retrieve(id);
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
    public MessageResponse chargeFromCustomer(CustomerChargeRequest request) throws StripeException {

        Stripe.apiKey = SECRET_KEY;

        Long passengerId = request.getPassengerId();
        User user = customerRepository.findById(passengerId).get();
        String customerId = user.getCustomerId();
        Map<String, Object> paymentIntentParams = new HashMap<>();
        paymentIntentParams.put("amount", request.getAmount() * 100);
        paymentIntentParams.put("currency", request.getCurrency());
        paymentIntentParams.put("customer", customerId);
        PaymentIntent intent = PaymentIntent.create(paymentIntentParams);
        intent.setPaymentMethod(customerId);
        PaymentIntentConfirmParams params =
                PaymentIntentConfirmParams.builder()
                        .setPaymentMethod("pm_card_visa")
                        .setReturnUrl("https://www.example.com")
                        .build();
        intent.confirm(params);
        return MessageResponse.builder().message("Successful").build();
    }


}
