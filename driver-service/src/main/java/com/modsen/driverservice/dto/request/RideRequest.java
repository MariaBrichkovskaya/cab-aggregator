package com.modsen.driverservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RideRequest {
    @JsonProperty("id")
    private long id;
}
