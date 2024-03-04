package com.modsen.driverservice.service;

import com.modsen.driverservice.dto.request.DriverRatingRequest;
import com.modsen.driverservice.dto.response.AverageDriverRatingResponse;
import com.modsen.driverservice.dto.response.DriverListRatingsResponse;
import com.modsen.driverservice.dto.response.DriverRatingResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.UUID;

public interface RatingService {
    DriverRatingResponse rateDriver(DriverRatingRequest driverRatingRequest, UUID driverId, OAuth2User principal);

    DriverListRatingsResponse getRatingsByDriverId(UUID driverId);

    AverageDriverRatingResponse getAverageDriverRating(UUID driverId);
}
