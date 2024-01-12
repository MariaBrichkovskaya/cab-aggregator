package com.modsen.rideservice.dto.response;

import com.modsen.rideservice.enums.RideStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class RideResponse {
    Long id;
    String pickUpAddress;
    String destinationAddress;
    Double price;
    PassengerResponse passengerResponse;
    DriverResponse driverResponse;
    RideStatus rideStatus;
    LocalDateTime date;
    String paymentMethod;
}
