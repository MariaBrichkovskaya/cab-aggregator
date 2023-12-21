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
import com.modsen.paymentservice.exception.PaymentException;
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
    public MessageResponse charge(ChargeRequest request) {
        Stripe.apiKey = SECRET_KEY;
        Charge charge;
        try {
            charge = Charge.create(
                    Map.of(
                            "amount", request.getAmount() * 100,
                            "currency", request.getCurrency(),
                            "source", request.getCardToken()
                    )
            );
        } catch (StripeException stripeException) {
            throw new PaymentException(stripeException.getMessage());
        }
        String message = "Payment successful. ID: " + charge.getId();
        return MessageResponse.builder().message(message).build();
    }


    @Override
    public TokenResponse create(CardRequest request) {
        Stripe.apiKey = PUBLIC_KEY;
        Map<String, Object> card = Map.of(
                "number", request.getCardNumber(),
                "exp_month", request.getExpMonth(),
                "exp_year", request.getExpYear(),
                "cvc", request.getCvc()
        );
        Token token;
        try {
            token = Token.create(Map.of("card", card));
        } catch (StripeException stripeException) {
            throw new PaymentException(stripeException.getMessage());
        }
        return TokenResponse.builder().token(token.getId()).build();
    }


    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
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

    private CustomerResponse createUser(CustomerCreateParams params, long id) {
        Stripe.apiKey = SECRET_KEY;
        Customer customer;
        try {
            customer = Customer.create(params);
        } catch (StripeException stripeException) {
            throw new PaymentException(stripeException.getMessage());
        }
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

    private void createPaymentMethod(String customerId) {
        Stripe.apiKey = SECRET_KEY;
        Map<String, Object> paymentMethodParams = Map.of(
                "type", "card",
                "card", Map.of("token", "tok_visa")
        );
        try {
            PaymentMethod paymentMethod = PaymentMethod.create(paymentMethodParams);
            paymentMethod.attach(Map.of("customer", customerId));
        } catch (StripeException stripeException) {
            throw new PaymentException(stripeException.getMessage());
        }

    }


    @Override
    public CustomerResponse retrieve(long id) {
        Stripe.apiKey = SECRET_KEY;
        checkCustomerNotFound(id);
        String customerId = customerRepository.findById(id).get().getCustomerId();
        try {
            Customer customer = Customer.retrieve(customerId);
            return CustomerResponse.builder()
                    .id(customer.getId())
                    .email(customer.getEmail())
                    .phone(customer.getPhone())
                    .name(customer.getName()).build();
        } catch (StripeException stripeException) {
            throw new PaymentException(stripeException.getMessage());
        }
    }

    @Override
    public BalanceResponse balance() {
        Stripe.apiKey = SECRET_KEY;
        try {
            Balance balance = Balance.retrieve();
            return BalanceResponse
                    .builder()
                    .amount(balance.getPending().get(0).getAmount())
                    .currency(balance.getPending().get(0).getCurrency())
                    .build();
        } catch (StripeException stripeException) {
            throw new PaymentException(stripeException.getMessage());
        }

    }

    @Override
    public ChargeResponse chargeFromCustomer(CustomerChargeRequest request) {
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

    private PaymentIntent createIntent(CustomerChargeRequest request, String customerId) {
        try {
            PaymentIntent intent = PaymentIntent.create(Map.of("amount", request.getAmount() * 100,
                    "currency", request.getCurrency(),
                    "customer", customerId));
            intent.setPaymentMethod(customerId);
            PaymentIntentConfirmParams params =
                    PaymentIntentConfirmParams.builder()
                            .setPaymentMethod(PaymentMethodEnum.VISA.getMethod())
                            .build();
            return intent.confirm(params);
        } catch (StripeException stripeException) {
            throw new PaymentException(stripeException.getMessage());
        }

    }

    private void updateBalance(String customerId, long amount) {
        try {
            Customer customer = Customer.retrieve(customerId);
            CustomerUpdateParams params =
                    CustomerUpdateParams.builder()
                            .setBalance(customer.getBalance() - amount * 100)
                            .build();
            Stripe.apiKey = SECRET_KEY;
            customer.update(params);
        } catch (StripeException stripeException) {
            throw new PaymentException(stripeException.getMessage());
        }

    }

    private void checkBalance(String customerId, long amount) {
        Customer customer;
        try {
            customer = Customer.retrieve(customerId);
        } catch (StripeException stripeException) {
            throw new PaymentException(stripeException.getMessage());
        }
        Long balance = customer.getBalance();
        if (balance < amount) {
            throw new BalanceException("Not enough money in the account");
        }
    }

}

