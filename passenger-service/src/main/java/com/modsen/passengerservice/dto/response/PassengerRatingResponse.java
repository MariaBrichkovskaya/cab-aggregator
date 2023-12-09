package com.modsen.passengerservice.dto.response;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PassengerRatingResponse {
    private long id;
    private long passengerId;
    private long driverId;
    private int score;
}
