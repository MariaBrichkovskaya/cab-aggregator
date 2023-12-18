package com.modsen.rideservice.dto.response;

import com.modsen.rideservice.enums.RideStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RideResponse {
    Long id;
    String pickUpAddress;
    String destinationAddress;
    Double price;
    PassengerResponse passengerResponse;
    DriverResponse driverResponse;
    RideStatus rideStatus;
    LocalDateTime date;
}
