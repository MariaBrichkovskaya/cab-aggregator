package com.modsen.driverservice.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class DriverListRatingsResponse {
    private List<DriverRatingResponse> driverRatings;
}
