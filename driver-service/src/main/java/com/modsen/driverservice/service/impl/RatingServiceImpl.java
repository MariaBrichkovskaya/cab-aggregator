package com.modsen.driverservice.service.impl;

import com.modsen.driverservice.dto.request.DriverRatingRequest;
import com.modsen.driverservice.dto.response.AverageDriverRatingResponse;
import com.modsen.driverservice.dto.response.DriverListRatingsResponse;
import com.modsen.driverservice.dto.response.DriverRatingResponse;
import com.modsen.driverservice.entity.Rating;
import com.modsen.driverservice.exception.NotFoundException;
import com.modsen.driverservice.repository.DriverRepository;
import com.modsen.driverservice.repository.RatingRepository;
import com.modsen.driverservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final RatingRepository driverRatingRepository;
    private final DriverRepository driverRepository;
    private final ModelMapper modelMapper;


    @Override
    public DriverRatingResponse rateDriver(DriverRatingRequest driverRatingRequest, long driverId) {
        Rating newDriverRating = toEntity(driverRatingRequest);
        newDriverRating.setDriver(driverRepository.findById(driverId)
                .orElseThrow(() -> new NotFoundException(driverId)));
        newDriverRating = driverRatingRepository.save(newDriverRating);
        return toDto(newDriverRating);
    }

    @Override
    public DriverListRatingsResponse getRatingsByDriverId(long driverId) {
        validateDriverExists(driverId);
        List<DriverRatingResponse> driverRatings = driverRatingRepository.getRatingsByDriverId(driverId)
                .stream()
                .map(this::toDto)
                .toList();
        return DriverListRatingsResponse.builder()
                .driverRatings(driverRatings)
                .build();
    }


    @Override
    public AverageDriverRatingResponse getAverageDriverRating(long driverId) {
        validateDriverExists(driverId);
        double averageRating = driverRatingRepository
                .getRatingsByDriverId(driverId)
                .stream()
                .mapToDouble(Rating::getScore)
                .average()
                .orElse(0.0);
        return AverageDriverRatingResponse.builder()
                .averageRating(Math.round(averageRating * 100.0) / 100.0)
                .driverId(driverId)
                .build();
    }

    public void validateDriverExists(long driverId) {
        driverRepository.findById(driverId)
                .orElseThrow(() -> new NotFoundException(driverId));
    }

    public Rating toEntity(DriverRatingRequest driverRatingRequest) {
        Rating driverRating = modelMapper.map(driverRatingRequest, Rating.class);
        driverRating.setId(null);
        return driverRating;
    }

    private DriverRatingResponse toDto(Rating driverRating) {
        return modelMapper.map(driverRating, DriverRatingResponse.class);
    }
}