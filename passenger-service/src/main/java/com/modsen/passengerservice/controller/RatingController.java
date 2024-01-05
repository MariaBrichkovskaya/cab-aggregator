package com.modsen.passengerservice.controller;

import com.modsen.passengerservice.dto.request.PassengerRatingRequest;
import com.modsen.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.modsen.passengerservice.dto.response.MessageResponse;
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
    public ResponseEntity<PassengerRatingResponse> ratePassenger(@Valid @RequestBody PassengerRatingRequest passengerRatingRequest,
                                                                 @PathVariable("id") long passengerId) {
        PassengerRatingResponse response = ratingService.ratePassenger(passengerRatingRequest, passengerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PassengerListRatingsResponse> getRatingsByPassengerId(@PathVariable("id") long passengerId) {
        return ResponseEntity.ok(ratingService.getRatingsByPassengerId(passengerId));
    }

    @GetMapping("/average")
    public ResponseEntity<AveragePassengerRatingResponse> getAveragePassengerRating(@PathVariable("id") long passengerId) {
        return ResponseEntity.ok(ratingService.getAveragePassengerRating(passengerId));
    }
}

