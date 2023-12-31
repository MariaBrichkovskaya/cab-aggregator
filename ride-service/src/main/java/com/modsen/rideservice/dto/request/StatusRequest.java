package com.modsen.rideservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusRequest {
    @NotBlank(message = "{status.not.empty.message}")
    @Pattern(regexp = "^(CREATED|ACCEPTED|REJECTED|FINISHED)$",message = "{invalid.status.message}")
    private String status;
}
