package com.modsen.driverservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverListRatingsResponse {
    List<DriverRatingResponse> driverRatings;
}
