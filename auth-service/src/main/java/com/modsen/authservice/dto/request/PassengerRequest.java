package com.modsen.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PassengerRequest {
    @NotBlank(message = "{name.not.empty.message}")
    String name;
    @NotBlank(message = "{surname.not.empty.message}")
    String surname;
    @Schema(example = "12345@gmail.com")
    @Email(message = "{invalid.email.message}")
    @NotBlank(message = "{email.not.empty.message}")
    String email;
    @Pattern(regexp = "^(80(29|44|33|25)\\d{7})$")
    @Schema(example = "80291111111")
    @NotBlank(message = "{phone.not.empty.message}")
    String phone;
}
