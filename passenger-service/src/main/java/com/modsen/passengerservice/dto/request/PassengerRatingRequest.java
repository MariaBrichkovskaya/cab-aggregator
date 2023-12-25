package com.modsen.passengerservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PassengerRatingRequest {
    @NotNull(message = "{score.not.empty.message}")
    @Min(value = 1,message = "{min.value.message}")
    @Max(value = 5,message = "{max.value.message}")
    Double score;
    @NotNull(message = "{driver.not.empty.message}")
    @Min(value = 1,message = "{min.value.message}")
    Long driverId;
}
