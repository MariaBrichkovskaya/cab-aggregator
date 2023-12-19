package com.modsen.paymentservice.service.impl;

import com.modsen.paymentservice.dto.request.CardRequest;
import com.modsen.paymentservice.dto.request.ChargeRequest;
import com.modsen.paymentservice.dto.request.CustomerChargeRequest;
import com.modsen.paymentservice.dto.request.CustomerRequest;
import com.modsen.paymentservice.dto.response.*;
import com.modsen.paymentservice.exception.AlreadyExistsException;
import com.modsen.paymentservice.exception.BalanceException;
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

import java.util.HashMap;
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
        Map<String, Object> params = new HashMap<>();
        params.put("amount", request.getAmount() * 100);
        params.put("currency", request.getCurrency());
        params.put("source", request.getCardToken());
        Charge charge = Charge.create(params);
        String message = "Payment successful. ID: " + charge.getId();
        return MessageResponse.builder().message(message).build();
    }

    @Override
    public TokenResponse create(CardRequest request) throws StripeException {
        Stripe.apiKey = PUBLIC_KEY;
        Map<String, Object> card = new HashMap<>();
        card.put("number", request.getCardNumber());
        card.put("exp_month", request.getExpMonth());
        card.put("exp_year", request.getExpYear());
        card.put("cvc", request.getCvc());
        Map<String, Object> params = new HashMap<>();
        params.put("card", card);
        Token token = Token.create(params);
        return TokenResponse.builder().token(token.getId()).build();
    }

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) throws StripeException {
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
        return createUser(params, request.getPassengerId());
    }
    private CustomerResponse createUser(CustomerCreateParams params,long id) throws StripeException {
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
    public CustomerResponse retrieve(long id) throws StripeException {
        Stripe.apiKey = SECRET_KEY;
        String customerId=customerRepository.findById(id).get().getCustomerId();
        Customer customer=Customer.retrieve(customerId);
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
        checkBalance(customerId, request.getAmount()*100);
        PaymentIntent intent=createIntent(request,customerId);
        updateBalance(customerId,request.getAmount());
        return ChargeResponse.builder().id(intent.getId())
                .amount(intent.getAmount()/100)
                .currency(intent.getCurrency()).build();
    }
    private PaymentIntent createIntent(CustomerChargeRequest request,String customerId) throws StripeException {
        Map<String, Object> paymentIntentParams = new HashMap<>();
        paymentIntentParams.put("amount", request.getAmount() * 100);
        paymentIntentParams.put("currency", request.getCurrency());
        paymentIntentParams.put("customer", customerId);
        PaymentIntent intent = PaymentIntent.create(paymentIntentParams);
        intent.setPaymentMethod(customerId);
        PaymentIntentConfirmParams params =
                PaymentIntentConfirmParams.builder()
                        .setPaymentMethod("pm_card_visa")
                        .build();
        return intent.confirm(params);
    }
    private void updateBalance(String customerId,long amount) throws StripeException {
        Customer customer=Customer.retrieve(customerId);
        CustomerUpdateParams params =
                CustomerUpdateParams.builder()
                        .setBalance(customer.getBalance()-amount*100)
                        .build();
        Stripe.apiKey = SECRET_KEY;
        customer.update(params);
    }
    private void checkBalance(String customerId,long amount) throws StripeException {
        Customer customer=Customer.retrieve(customerId);
        Long balance=customer.getBalance();
        if(balance<amount)
            throw new BalanceException("Not enough money in the account");

    }

}

