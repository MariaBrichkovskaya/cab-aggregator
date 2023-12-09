package com.modsen.driverservice.dto.response;

import lombok.*;


@Getter
@Setter
@Builder
public class AverageDriverRatingResponse {
    private double averageRating;
    private long driverId;
}
