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
    @NotBlank(message = "{number.not.empty.message}")
    String cardNumber;
    @NotNull(message = "{month.not.empty.message}")
    int expMonth;
    @NotNull(message = "{year.not.empty.message}")
    int expYear;
    @NotBlank(message = "{cvc.not.empty.message}")
    @Length(max = 3,message = "{cvc.length.message}")
    String cvc;
}
