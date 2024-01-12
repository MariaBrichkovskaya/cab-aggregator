package com.modsen.rideservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UpdateRideRequest {
    @NotBlank(message = "{address.not.empty.message}")
    String pickUpAddress;
    @NotBlank(message = "{address.not.empty.message}")
    String destinationAddress;
    @Min(value = 1, message = "{min.value.message}")
    @NotNull(message = "{price.not.empty.message}")
    Double price;
    @Range(min = 1, message = "{min.value.message}")
    @NotNull(message = "{passenger.not.empty.message}")
    Long passengerId;
}
