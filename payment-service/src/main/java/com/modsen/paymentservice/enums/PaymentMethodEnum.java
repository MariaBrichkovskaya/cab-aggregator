package com.modsen.paymentservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethodEnum {

    VISA("pm_card_visa");
    private final String method;
}
