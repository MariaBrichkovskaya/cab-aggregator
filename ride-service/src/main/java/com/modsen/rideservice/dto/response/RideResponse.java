package com.modsen.rideservice.dto.response;

import com.modsen.rideservice.enums.PaymentMethod;
import com.modsen.rideservice.enums.RideStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
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
