package com.modsen.rideservice.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CreateRideRequest {
    @NotBlank(message = "{address.not.empty.message}")
    String pickUpAddress;
    @NotBlank(message = "{address.not.empty.message}")
    String destinationAddress;
    @NotNull(message = "{payment.not.empty.message}")
    @Pattern(regexp = "CARD|CASH", message = "{invalid.payment.method.message}")
    String paymentMethod;

}

