package com.modsen.rideservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RideRequest {
    @NotBlank(message = "Pick-up address is mandatory")
    String pickUpAddress;
    @NotBlank(message = "Destination address is mandatory")
    String destinationAddress;
    @Min(value = 1)
    //
    Double price;
    //
    @Min(value = 1L)
    Long passengerId;
    //
    @Min(value = 1L)
    Long driverId;

}

