package com.modsen.rideservice.dto.request;

import lombok.*;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
public class EditDriverStatusRequest {
    private long driverId;
}
