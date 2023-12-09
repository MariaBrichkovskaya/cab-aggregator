package com.modsen.passengerservice.service.impl;

import com.modsen.passengerservice.dto.request.PassengerRatingRequest;
import com.modsen.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.modsen.passengerservice.dto.response.PassengerListRatingsResponse;
import com.modsen.passengerservice.dto.response.PassengerRatingResponse;
import com.modsen.passengerservice.entity.Rating;
import com.modsen.passengerservice.exception.NotFoundException;
import com.modsen.passengerservice.repository.PassengerRepository;
import com.modsen.passengerservice.repository.RatingRepository;
import com.modsen.passengerservice.service.RatingService;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final PassengerRepository passengerRepository;
    private final ModelMapper modelMapper;


    @Override
    public PassengerRatingResponse ratePassenger(PassengerRatingRequest passengerRatingRequest, long passengerId) {
        Rating newPassengerRating = toEntity(passengerRatingRequest);
        newPassengerRating.setPassenger(passengerRepository.findById(passengerId)
                .orElseThrow(() -> new NotFoundException(passengerId)));
        newPassengerRating = ratingRepository.save(newPassengerRating);
        return toDto(newPassengerRating);
    }

    @Override
    public PassengerListRatingsResponse getRatingsByPassengerId(long passengerId) {
        validatePassengerExists(passengerId);
        List<PassengerRatingResponse> passengerRatings = ratingRepository.getRatingsByPassengerId(passengerId)
                .stream()
                .map(this::toDto)
                .toList();
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
                .orElse(0.0);
        return AveragePassengerRatingResponse.builder()
                .averageRating(Math.round(averageRating * 100.0) / 100.0)
                .passengerId(passengerId)
                .build();
    }

    public void validatePassengerExists(long passengerId) {
        passengerRepository.findById(passengerId)
                .orElseThrow(() -> new NotFoundException(passengerId));
    }

    public Rating toEntity(PassengerRatingRequest passengerRatingRequest) {
        Rating driverRating = modelMapper.map(passengerRatingRequest, Rating.class);
        driverRating.setId(null);
        return driverRating;
    }

    private PassengerRatingResponse toDto(Rating rating) {
        return modelMapper.map(rating, PassengerRatingResponse.class);
    }
}