package com.modsen.paymentservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChargeRequest {
    @NotNull(message = "Amount is mandatory")
    @Range(min = 1, max = 1000000,message = "Amount should be between 100 and 1000000")
    long amount;
    @NotBlank(message = "Currency is mandatory")
    String currency;
    @NotBlank(message = "Token is mandatory")
    String cardToken;
}

