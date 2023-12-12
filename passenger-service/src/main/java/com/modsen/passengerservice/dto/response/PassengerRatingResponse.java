package com.modsen.passengerservice.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PassengerRatingResponse {
    long id;
    long passengerId;
    long driverId;
    int score;
}
