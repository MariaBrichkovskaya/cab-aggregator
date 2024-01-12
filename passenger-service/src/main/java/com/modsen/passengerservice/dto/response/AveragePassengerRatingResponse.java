package com.modsen.passengerservice.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class AveragePassengerRatingResponse {
    double averageRating;
    long passengerId;
}
