package com.modsen.passengerservice.service;

import com.modsen.passengerservice.dto.request.PassengerRatingRequest;
import com.modsen.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.modsen.passengerservice.dto.response.PassengerListRatingsResponse;
import com.modsen.passengerservice.dto.response.PassengerRatingResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.UUID;

public interface RatingService {
    PassengerRatingResponse ratePassenger(PassengerRatingRequest passengerRatingRequest, UUID passengerId, OAuth2User principal);

    PassengerListRatingsResponse getRatingsByPassengerId(UUID passengerId);

    AveragePassengerRatingResponse getAveragePassengerRating(UUID passengerId);
}
