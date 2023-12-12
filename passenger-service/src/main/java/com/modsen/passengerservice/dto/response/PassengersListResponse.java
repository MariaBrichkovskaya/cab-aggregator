package com.modsen.passengerservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PassengersListResponse {
    List<PassengerResponse> passengers;
}
