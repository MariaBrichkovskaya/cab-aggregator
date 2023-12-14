package com.modsen.paymentservice.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CustomersListResponse {
    List<CustomerResponse> customers;
}
