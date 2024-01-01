package com.modsen.driverservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class EditDriverStatusRequest {
    @JsonProperty("driverId")
    long driverId;
}
