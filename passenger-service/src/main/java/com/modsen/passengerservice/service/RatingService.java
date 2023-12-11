package com.modsen.passengerservice.service;

import com.modsen.passengerservice.dto.request.PassengerRatingRequest;
import com.modsen.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.modsen.passengerservice.dto.response.PassengerListRatingsResponse;
import com.modsen.passengerservice.dto.response.PassengerRatingResponse;

public interface RatingService {
    void ratePassenger(PassengerRatingRequest passengerRatingRequest, long passengerId);

    PassengerListRatingsResponse getRatingsByPassengerId(long driverId);

    AveragePassengerRatingResponse getAveragePassengerRating(long driverId);
}
