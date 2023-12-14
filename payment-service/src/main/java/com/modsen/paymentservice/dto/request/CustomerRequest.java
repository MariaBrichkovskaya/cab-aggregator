package com.modsen.paymentservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerRequest {
    String name;
    @Email
    String email;
    @Pattern(regexp = "^(80(29|44|33|25)\\d{7})$")
    @NotBlank(message = "Phone is mandatory")
    String phone;
    long passengerId;
    long amount;
}
