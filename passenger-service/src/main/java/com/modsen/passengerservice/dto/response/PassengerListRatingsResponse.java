package com.modsen.passengerservice.dto.response;


import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class PassengerListRatingsResponse {
    private List<PassengerRatingResponse> passengerRatings;
}
