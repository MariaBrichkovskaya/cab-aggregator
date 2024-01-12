package com.modsen.driverservice.dto.response;

import com.modsen.driverservice.enums.Status;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Builder
public class DriverResponse {
    Long id;
    String name;
    String surname;
    String phone;
    Double rating;
    Status status;
}
