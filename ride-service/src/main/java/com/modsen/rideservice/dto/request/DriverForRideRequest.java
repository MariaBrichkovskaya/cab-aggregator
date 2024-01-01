package com.modsen.rideservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class DriverForRideRequest {
    @JsonProperty("driverId")
    Long driverId;
    @JsonProperty("rideId")
    Long rideId;
}
