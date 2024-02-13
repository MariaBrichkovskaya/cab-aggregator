package com.modsen.passengerservice.service.impl;

import com.modsen.passengerservice.dto.request.PassengerRatingRequest;
import com.modsen.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.modsen.passengerservice.dto.response.DriverResponse;
import com.modsen.passengerservice.dto.response.PassengerListRatingsResponse;
import com.modsen.passengerservice.dto.response.PassengerRatingResponse;
import com.modsen.passengerservice.entity.Rating;
import com.modsen.passengerservice.exception.NotFoundException;
import com.modsen.passengerservice.repository.PassengerRepository;
import com.modsen.passengerservice.repository.RatingRepository;
import com.modsen.passengerservice.service.DriverService;
import com.modsen.passengerservice.service.RatingService;
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
    private final DriverService driverService;
    private final double DEFAULT_RATING = 5.0;

    private DriverResponse getDriver(long id) {
        return driverService.getDriver(id);
    }

    @Override
    public PassengerRatingResponse ratePassenger(PassengerRatingRequest passengerRatingRequest, long passengerId) {
        Rating newPassengerRating = toEntity(passengerRatingRequest);
        newPassengerRating.setPassenger(passengerRepository.findByIdAndActiveIsTrue(passengerId)
                .orElseThrow(() -> {
                            log.error("driver with id {} is not found", passengerId);
                            return new NotFoundException(passengerId);
                        }
                ));
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
                .orElse(DEFAULT_RATING);
        log.info("Retrieving average rating for passenger {}", passengerId);
        return AveragePassengerRatingResponse.builder()
                .averageRating(Math.round(averageRating * 100.0) / 100.0)
                .passengerId(passengerId)
                .build();
    }

    private void validatePassengerExists(long passengerId) {
        passengerRepository.findById(passengerId)
                .orElseThrow(() -> {
                            log.error("driver with id {} is not found", passengerId);
                            return new NotFoundException(passengerId);
                        }
                );
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
