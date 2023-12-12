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
    @NotNull(message = "Score is mandatory")
    @Min(value = 1,message = "Min value is 1")
    @Max(value = 5,message = "Max value is 1")
    Double score;
    @NotNull(message = "Driver is mandatory")
    @Min(value = 1,message = "Min value is 1")
    Long driverId;
}
