package com.modsen.driverservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverRatingResponse {
    private long id;
    private long passengerId;
    private long driverId;
    private int score;
}
