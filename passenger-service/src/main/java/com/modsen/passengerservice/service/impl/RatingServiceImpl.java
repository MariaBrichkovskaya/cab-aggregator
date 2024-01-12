package com.modsen.passengerservice.service.impl;

import com.modsen.passengerservice.client.DriverFeignClient;
import com.modsen.passengerservice.dto.request.PassengerRatingRequest;
import com.modsen.passengerservice.dto.response.AveragePassengerRatingResponse;
import com.modsen.passengerservice.dto.response.DriverResponse;
import com.modsen.passengerservice.dto.response.PassengerListRatingsResponse;
import com.modsen.passengerservice.dto.response.PassengerRatingResponse;
import com.modsen.passengerservice.entity.Rating;
import com.modsen.passengerservice.exception.NotFoundException;
import com.modsen.passengerservice.repository.PassengerRepository;
import com.modsen.passengerservice.repository.RatingRepository;
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
        PassengerRatingResponse response = toDto(ratingRepository.save(newPassengerRating));
        response.setDriverResponse(getDriver(passengerRatingRequest.getDriverId()));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PassengerListRatingsResponse getRatingsByPassengerId(long passengerId) {
        validatePassengerExists(passengerId);
        List<PassengerRatingResponse> passengerRatings = ratingRepository.getRatingsByPassengerId(passengerId)
                .stream()
                .map(rating -> {
                    PassengerRatingResponse response = toDto(rating);
                    response.setDriverResponse(getDriver(rating.getDriverId()));
                    return response;
                })
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

    private Rating toEntity(PassengerRatingRequest passengerRatingRequest) {
        Rating driverRating = modelMapper.map(passengerRatingRequest, Rating.class);
        driverRating.setId(null);
        return driverRating;
    }

    private PassengerRatingResponse toDto(Rating rating) {
        return modelMapper.map(rating, PassengerRatingResponse.class);
    }
}
