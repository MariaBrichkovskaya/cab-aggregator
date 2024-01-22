package com.modsen.driverservice.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PassengerResponse {
    Long id;
    String name;
    String surname;
    String email;
    String phone;
    Double rating;
    boolean active;
}
