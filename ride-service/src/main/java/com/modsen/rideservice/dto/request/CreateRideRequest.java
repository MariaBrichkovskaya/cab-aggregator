package com.modsen.rideservice.dto.request;


import com.modsen.rideservice.enums.PaymentMethod;
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
public class CreateRideRequest {
    @NotBlank(message = "Pick-up address is mandatory")
    String pickUpAddress;
    @NotBlank(message = "Destination address is mandatory")
    String destinationAddress;
    @Range(min = 1, message = "Min value is 1")
    @NotNull(message = "Passenger address is mandatory")
    Long passengerId;
    @NotNull(message = "Payment method is mandatory")
    @Pattern(regexp = "CARD|CASH", message = "Invalid payment method")
    String paymentMethod;

}

