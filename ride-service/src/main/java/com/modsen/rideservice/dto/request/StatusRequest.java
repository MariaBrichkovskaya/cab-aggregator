package com.modsen.rideservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusRequest {
    @NotBlank(message = "Status is mandatory")
    @Pattern(regexp = "^(CREATED|ACCEPTED|REJECTED|FINISHED)$",message = "Statuses are CREATED ACCEPTED REJECTED FINISHED")
    private String status;
}
