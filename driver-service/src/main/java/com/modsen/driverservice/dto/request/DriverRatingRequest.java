package com.modsen.driverservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class DriverRatingRequest {
    @NotNull(message = "{score.not.empty.message}")
    @Min(value = 1, message = "{min.value.message}")
    @Max(value = 5, message = "{max.value.message}")
    Integer score;
}
