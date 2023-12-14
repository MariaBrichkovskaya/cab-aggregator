package com.modsen.paymentservice.dto.request;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ChargeRequest {

    private int amount;
    private String currency;
    private String cardToken;

}

