package com.modsen.driverservice.controller;

import com.modsen.driverservice.dto.request.DriverRatingRequest;
import com.modsen.driverservice.dto.response.AverageDriverRatingResponse;
import com.modsen.driverservice.dto.response.DriverListRatingsResponse;
import com.modsen.driverservice.dto.response.DriverRatingResponse;
import com.modsen.driverservice.dto.response.MessageResponse;
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
    public ResponseEntity<MessageResponse> rateDriver(@Valid @RequestBody DriverRatingRequest driverRatingRequest,
                                                      @PathVariable("id") long driverId) {
        ratingService.rateDriver(driverRatingRequest, driverId);
        String message = "Rate driver with id " + driverId;
        return ResponseEntity.ok(MessageResponse.builder().message(message).build());
    }

    @GetMapping
    public ResponseEntity<DriverListRatingsResponse> getRatingsByDriverId(@PathVariable("id") long driverId) {
        return ResponseEntity.ok(ratingService.getRatingsByDriverId(driverId));
    }

    @GetMapping("/average")
    public ResponseEntity<AverageDriverRatingResponse> getAverageDriverRating(@PathVariable("id") long driverId) {
        return ResponseEntity.ok(ratingService.getAverageDriverRating(driverId));
    }
}
