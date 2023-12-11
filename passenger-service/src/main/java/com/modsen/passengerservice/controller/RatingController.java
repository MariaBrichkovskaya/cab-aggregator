package com.modsen.passengerservice.controller;

import com.modsen.passengerservice.dto.request.PassengerRatingRequest;
import com.modsen.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.modsen.passengerservice.dto.response.PassengerListRatingsResponse;
import com.modsen.passengerservice.dto.response.PassengerRatingResponse;
import com.modsen.passengerservice.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/passengers/{id}/rating")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<String> ratePassenger(@Valid @RequestBody PassengerRatingRequest passengerRatingRequest,
                                                                 @PathVariable("id") long passengerId) {
        ratingService.ratePassenger(passengerRatingRequest, passengerId);
        return ResponseEntity.ok("Rate passenger with id " + passengerId);
    }

    @GetMapping
    public PassengerListRatingsResponse getRatingsByPassengerId(@PathVariable("id") long passengerId) {
        return ratingService.getRatingsByPassengerId(passengerId);
    }

    @GetMapping("/average")
    public AveragePassengerRatingResponse getAveragePassengerRating(@PathVariable("id") long passengerId) {
        return ratingService.getAveragePassengerRating(passengerId);
    }
}

