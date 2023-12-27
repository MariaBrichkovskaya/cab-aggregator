package com.modsen.driverservice.dto.request;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateRideRequest {
    long id;
    String pickUpAddress;
    String destinationAddress;
    Long passengerId;

}

