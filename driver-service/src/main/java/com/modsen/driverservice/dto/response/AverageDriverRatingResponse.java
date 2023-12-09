package com.modsen.driverservice.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AverageDriverRatingResponse {
    private double averageRating;
    private long driverId;
}
