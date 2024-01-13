package com.modsen.rideservice.dto.response;

import com.modsen.rideservice.enums.RideStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@AllArgsConstructor
@NoArgsConstructor
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
