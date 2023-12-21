package com.modsen.paymentservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
public class CardRequest {
    @NotBlank(message = "Card number is mandatory")
    String cardNumber;
    @NotNull(message = "Expiration month is mandatory")
    int expMonth;
    @NotNull(message = "Expiration year is mandatory")
    int expYear;
    @NotBlank(message = "Cvc is mandatory")
    @Length(max = 3,message = "Max length is 3")
    String cvc;
}
