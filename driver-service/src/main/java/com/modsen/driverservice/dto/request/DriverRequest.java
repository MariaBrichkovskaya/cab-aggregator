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
    @NotBlank(message = "Name is mandatory")
    String name;
    @NotBlank(message = "Surname is mandatory")
    String surname;
    @Pattern(regexp = "^(80(29|44|33|25)\\d{7})$")
    @NotBlank(message = "Phone is mandatory")
    String phone;
}
