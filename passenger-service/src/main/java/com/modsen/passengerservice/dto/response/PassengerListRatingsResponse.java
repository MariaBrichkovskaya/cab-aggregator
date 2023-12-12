package com.modsen.passengerservice.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PassengerListRatingsResponse {
    List<PassengerRatingResponse> passengerRatings;
}
