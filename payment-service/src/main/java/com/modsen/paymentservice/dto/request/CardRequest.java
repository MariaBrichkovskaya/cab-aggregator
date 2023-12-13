package com.modsen.paymentservice.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
public class CardRequest {
    String cardNumber;
    int expMonth;
    int expYear;
    String cvc;
}
