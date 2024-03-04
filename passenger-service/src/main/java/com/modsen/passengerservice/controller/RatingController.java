package com.modsen.passengerservice.controller;

import com.modsen.passengerservice.dto.request.PassengerRatingRequest;
import com.modsen.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.modsen.passengerservice.dto.response.PassengerListRatingsResponse;
import com.modsen.passengerservice.dto.response.PassengerRatingResponse;
import com.modsen.passengerservice.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/passengers/{id}/rating")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PreAuthorize("hasAnyRole('ROLE_DRIVER')")
    @PostMapping
    public ResponseEntity<PassengerRatingResponse> ratePassenger(@Valid @RequestBody PassengerRatingRequest passengerRatingRequest,
                                                                 @PathVariable("id") UUID passengerId,
                                                                 @AuthenticationPrincipal OAuth2User principal) {
        PassengerRatingResponse response = ratingService.ratePassenger(passengerRatingRequest, passengerId, principal);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PassengerListRatingsResponse> getRatingsByPassengerId(@PathVariable("id") UUID passengerId) {
        return ResponseEntity.ok(ratingService.getRatingsByPassengerId(passengerId));
    }

    @GetMapping("/average")
    public ResponseEntity<AveragePassengerRatingResponse> getAveragePassengerRating(@PathVariable("id") UUID passengerId) {
        return ResponseEntity.ok(ratingService.getAveragePassengerRating(passengerId));
    }
}

