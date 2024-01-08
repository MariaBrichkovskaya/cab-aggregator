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
public class CustomerChargeRequest {
    @NotNull(message = "{amount.not.empty.message}")
    @Range(min = 1, max = 10000,message = "{amount.range.message}")
    long amount;
    @NotBlank(message = "{currency.not.empty.message}")
    String currency;
    @NotNull(message = "{passenger.not.empty.message}")
    @Range(min = 1, message = "{min.value.message}")
    long passengerId;
}
