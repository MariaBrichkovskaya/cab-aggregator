package com.modsen.passengerservice.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
public class AveragePassengerRatingResponse {
    private double averageRating;
    private long passengerId;
}
