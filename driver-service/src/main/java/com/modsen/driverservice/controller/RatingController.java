package com.modsen.driverservice.controller;

import com.modsen.driverservice.dto.request.DriverRatingRequest;
import com.modsen.driverservice.dto.response.AverageDriverRatingResponse;
import com.modsen.driverservice.dto.response.DriverListRatingsResponse;
import com.modsen.driverservice.dto.response.DriverRatingResponse;
import com.modsen.driverservice.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers/{id}/rating")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<String> rateDriver(@Valid @RequestBody DriverRatingRequest driverRatingRequest,
                                     @PathVariable("id") long driverId) {
        ratingService.rateDriver(driverRatingRequest, driverId);
        return ResponseEntity.ok("Rate passenger with id " + driverId);
    }

    @GetMapping
    public DriverListRatingsResponse getRatingsByDriverId(@PathVariable("id") long driverId) {
        return ratingService.getRatingsByDriverId(driverId);
    }

    @GetMapping("/average")
    public AverageDriverRatingResponse getAverageDriverRating(@PathVariable("id") long driverId) {
        return ratingService.getAverageDriverRating(driverId);
    }
}
