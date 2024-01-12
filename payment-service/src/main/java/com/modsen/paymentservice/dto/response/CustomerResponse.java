package com.modsen.paymentservice.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@AllArgsConstructor
public class CustomerResponse {
    String id;
    String email;
    String phone;
    String name;
}
