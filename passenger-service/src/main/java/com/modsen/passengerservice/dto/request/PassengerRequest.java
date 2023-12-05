package com.modsen.passengerservice.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PassengerRequest {
    String name;
    String surname;
    String email;
    String phone;
    Double rating = 5.0;
}
