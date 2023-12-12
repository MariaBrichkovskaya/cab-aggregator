package com.modsen.passengerservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AveragePassengerRatingResponse {
    double averageRating;
    long passengerId;
}
