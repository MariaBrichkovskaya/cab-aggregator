package com.modsen.passengerservice.service.impl;

import com.modsen.passengerservice.client.*;
import com.modsen.passengerservice.dto.request.*;
import com.modsen.passengerservice.dto.response.*;
import com.modsen.passengerservice.entity.*;
import com.modsen.passengerservice.exception.*;
import com.modsen.passengerservice.repository.*;
import com.modsen.passengerservice.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final PassengerRepository passengerRepository;
    private final ModelMapper modelMapper;
    private final DriverFeignClient driverFeignClient;

    private DriverResponse getDriver(long id) {
        return driverFeignClient.getDriver(id);
    }

    @Override
    public PassengerRatingResponse ratePassenger(PassengerRatingRequest passengerRatingRequest, long passengerId) {
        Rating newPassengerRating = toEntity(passengerRatingRequest);
        newPassengerRating.setPassenger(passengerRepository.findById(passengerId)
                .orElseThrow(() -> new NotFoundException(passengerId)));
        log.info("Update rating for passenger {}", passengerId);
        return toDto(ratingRepository.save(newPassengerRating));
    }

    @Override
    @Transactional(readOnly = true)
    public PassengerListRatingsResponse getRatingsByPassengerId(long passengerId) {
        validatePassengerExists(passengerId);
        List<PassengerRatingResponse> passengerRatings = ratingRepository.getRatingsByPassengerId(passengerId)
                .stream()
                .map(this::toDto)
                .toList();
        log.info("Retrieving rating for passenger {}", passengerId);
        return PassengerListRatingsResponse.builder()
                .passengerRatings(passengerRatings)
                .build();
    }


    @Override
    public AveragePassengerRatingResponse getAveragePassengerRating(long passengerId) {
        validatePassengerExists(passengerId);
        double averageRating = ratingRepository
                .getRatingsByPassengerId(passengerId)
                .stream()
                .mapToDouble(Rating::getScore)
                .average()
                .orElse(5.0);
        log.info("Retrieving average rating for passenger {}", passengerId);
        return AveragePassengerRatingResponse.builder()
                .averageRating(Math.round(averageRating * 100.0) / 100.0)
                .passengerId(passengerId)
                .build();
    }

    private void validatePassengerExists(long passengerId) {
        passengerRepository.findById(passengerId)
                .orElseThrow(() -> new NotFoundException(passengerId));
    }

    public Rating toEntity(PassengerRatingRequest passengerRatingRequest) {
        return modelMapper.map(passengerRatingRequest, Rating.class);
    }

    private PassengerRatingResponse toDto(Rating rating) {
        PassengerRatingResponse response = modelMapper.map(rating, PassengerRatingResponse.class);
        response.setDriverResponse(getDriver(rating.getDriverId()));
        return response;
    }
}
