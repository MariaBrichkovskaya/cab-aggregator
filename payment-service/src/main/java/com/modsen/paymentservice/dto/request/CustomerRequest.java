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

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerRequest {
    @NotBlank(message = "{name.not.empty.message}")
    String name;
    @Schema(example = "12345@gmail.com")
    @Email(message = "{invalid.email.message}")
    @NotBlank(message = "{email.not.empty.message}")
    String email;
    @Pattern(regexp = "^(80(29|44|33|25)\\d{7})$")
    @NotBlank(message = "{phone.not.empty.message}")
    String phone;
    @NotNull(message = "{passenger.not.empty.message}")
    UUID passengerId;
    @NotNull(message = "{amount.not.empty.message}")
    @Range(min = 100, max = 1000000, message = "{amount.range.message}")
    long amount;
}
