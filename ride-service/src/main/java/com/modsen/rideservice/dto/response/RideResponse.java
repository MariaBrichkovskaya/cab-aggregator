package com.modsen.rideservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.modsen.rideservice.enums.Status;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RideResponse {
    Long id;
    String pickUpAddress;
    String destinationAddress;
    Double price;
    Long passengerId;
    Long driverId;
    Status status;
    LocalDateTime date;
}
