package com.modsen.driverservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AverageDriverRatingResponse {
    double averageRating;
    long driverId;
}
