package com.modsen.driverservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DriverRatingResponse {
    private long id;
    private long passengerId;
    private long driverId;
    private int score;
}
