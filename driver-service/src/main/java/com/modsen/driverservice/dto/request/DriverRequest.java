package com.modsen.driverservice.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverRequest {
    @NotBlank(message = "{name.not.empty.message}")
    String name;
    @NotBlank(message = "{surname.not.empty.message}")
    String surname;
    @Pattern(regexp = "^(80(29|44|33|25)\\d{7})$")
    @NotBlank(message = "{phone.not.empty.message}")
    String phone;
}
