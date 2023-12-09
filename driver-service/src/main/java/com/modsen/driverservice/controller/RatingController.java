package com.modsen.driverservice.controller;

import com.modsen.driverservice.dto.request.DriverRatingRequest;
import com.modsen.driverservice.dto.response.AverageDriverRatingResponse;
import com.modsen.driverservice.dto.response.DriverListRatingsResponse;
import com.modsen.driverservice.dto.response.DriverRatingResponse;
import com.modsen.driverservice.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rating/{id}")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingServiceatingService;

    @PostMapping
    public DriverRatingResponse rateDriver(@Valid @RequestBody DriverRatingRequest driverRatingRequest,
                                           @PathVariable("id") long driverId) {
        return ratingServiceatingService.rateDriver(driverRatingRequest, driverId);
    }

    @GetMapping
    public DriverListRatingsResponse getRatingsByDriverId(@PathVariable("id") long driverId) {
        return ratingServiceatingService.getRatingsByDriverId(driverId);
    }

    @GetMapping("/average")
    public AverageDriverRatingResponse getAverageDriverRating(@PathVariable("id") long driverId) {
        return ratingServiceatingService.getAverageDriverRating(driverId);
    }
}
