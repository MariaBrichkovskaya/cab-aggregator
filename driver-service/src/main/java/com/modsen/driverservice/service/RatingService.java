package com.modsen.driverservice.service;

import com.modsen.driverservice.dto.request.DriverRatingRequest;
import com.modsen.driverservice.dto.response.AverageDriverRatingResponse;
import com.modsen.driverservice.dto.response.DriverRatingResponse;
import com.modsen.driverservice.dto.response.DriverListRatingsResponse;

public interface RatingService {
    DriverRatingResponse rateDriver(DriverRatingRequest driverRatingRequest, long driverId);

    DriverListRatingsResponse getRatingsByDriverId(long driverId);

    AverageDriverRatingResponse getAverageDriverRating(long driverId);
}
