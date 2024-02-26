package com.modsen.driverservice.controller;

import com.modsen.driverservice.dto.request.DriverRatingRequest;
import com.modsen.driverservice.dto.response.AverageDriverRatingResponse;
import com.modsen.driverservice.dto.response.DriverListRatingsResponse;
import com.modsen.driverservice.dto.response.DriverRatingResponse;
import com.modsen.driverservice.service.RatingService;
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
@RequestMapping("/api/v1/drivers/{id}/rating")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_PASSENGER')")
    public ResponseEntity<DriverRatingResponse> rateDriver(@Valid @RequestBody DriverRatingRequest driverRatingRequest,
                                                           @PathVariable("id") UUID driverId,
                                                           @AuthenticationPrincipal OAuth2User principal) {
        DriverRatingResponse response = ratingService.rateDriver(driverRatingRequest, driverId, principal);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<DriverListRatingsResponse> getRatingsByDriverId(@PathVariable("id") UUID driverId) {
        return ResponseEntity.ok(ratingService.getRatingsByDriverId(driverId));
    }

    @GetMapping("/average")
    public ResponseEntity<AverageDriverRatingResponse> getAverageDriverRating(@PathVariable("id") UUID driverId) {
        return ResponseEntity.ok(ratingService.getAverageDriverRating(driverId));
    }
}
