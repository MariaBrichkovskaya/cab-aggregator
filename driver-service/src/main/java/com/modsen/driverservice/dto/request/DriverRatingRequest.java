package com.modsen.driverservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class DriverRatingRequest {
    @NotNull(message = "Score is mandatory")
    @Min(value = 1, message = "Min value is 1")
    @Max(value = 5, message = "Min value is 5")
    Integer score;

    @NotNull(message = "Passenger is mandatory")
    @Min(value = 1, message = "Min value is 1")
    private Long passengerId;
}
