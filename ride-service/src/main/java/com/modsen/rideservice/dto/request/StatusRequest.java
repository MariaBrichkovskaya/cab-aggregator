package com.modsen.rideservice.dto.request;

import com.modsen.rideservice.enums.Status;
import lombok.Getter;

@Getter
public class StatusRequest {
    private Status status;
}
