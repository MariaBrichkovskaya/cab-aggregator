package com.modsen.paymentservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerRequest {
    @NotBlank(message = "Name is mandatory")
    String name;
    @Schema(example = "12345@gmail.com")
    @Email(message = "Email is not valid")
    @NotBlank(message = "Email is mandatory")
    String email;
    @Pattern(regexp = "^(80(29|44|33|25)\\d{7})$")
    @NotBlank(message = "Phone is mandatory")
    String phone;
    @NotNull(message = "Passenger is mandatory")
    @Range(min = 1, message = "Min value is 1")
    long passengerId;
    @NotNull(message = "Amount is mandatory")
    @Range(min = 100, max = 1000000,message = "Amount should be between 100 and 1000000")
    long amount;
}
