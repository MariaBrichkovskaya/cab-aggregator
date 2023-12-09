package com.modsen.passengerservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PassengerRequest {
    @NotBlank(message = "Name is mandatory")
    String name;
    @NotBlank(message = "Surname is mandatory")
    String surname;
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    @Schema(example = "12345@gmail.com")
    @NotBlank(message = "Email is mandatory")
    String email;
    @Pattern(regexp = "^(80(29|44|33|25)\\d{7})$")
    @Schema(example = "80291111111")
    @NotBlank(message = "Phone is mandatory")
    String phone;
    //Double rating = 5.0;
}
