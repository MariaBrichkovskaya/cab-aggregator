package com.modsen.rideservice.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RidesListResponse {
    List<RideResponse> rides;
}

