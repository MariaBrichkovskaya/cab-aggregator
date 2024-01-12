package com.modsen.rideservice.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Range;


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
    @Range(min = 1, message = "{min.value.message}")
    @NotNull(message = "{passenger.not.empty.message}")
    Long passengerId;
    @NotNull(message = "{payment.not.empty.message}")
    @Pattern(regexp = "CARD|CASH", message = "{invalid.payment.method.message}")
    String paymentMethod;

}

