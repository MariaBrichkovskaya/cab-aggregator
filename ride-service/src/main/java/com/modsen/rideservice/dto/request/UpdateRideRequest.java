package com.modsen.rideservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Range;
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRideRequest {
    @NotBlank(message = "Pick-up address is mandatory")
    String pickUpAddress;
    @NotBlank(message = "Destination address is mandatory")
    String destinationAddress;
    @Min(value = 1,message = "Min value is 1")
    @NotNull(message = "Price is mandatory")
    Double price;
    @Range(min = 1, message = "Min value is 1")
    @NotNull(message = "Passenger address is mandatory")
    Long passengerId;
}