package com.modsen.driverservice.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverRatingResponse {
    long id;
    long passengerId;
    long driverId;
    int score;
}
