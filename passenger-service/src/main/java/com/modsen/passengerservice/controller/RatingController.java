package com.modsen.passengerservice.controller;

import com.modsen.passengerservice.dto.request.PassengerRatingRequest;
import com.modsen.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.modsen.passengerservice.dto.response.PassengerListRatingsResponse;
import com.modsen.passengerservice.dto.response.PassengerRatingResponse;
import com.modsen.passengerservice.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rating/{id}")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PostMapping
    public PassengerRatingResponse ratePassenger(@Valid @RequestBody PassengerRatingRequest passengerRatingRequest,
                                              @PathVariable("id") long passengerId) {
        return ratingService.ratePassenger(passengerRatingRequest, passengerId);
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

