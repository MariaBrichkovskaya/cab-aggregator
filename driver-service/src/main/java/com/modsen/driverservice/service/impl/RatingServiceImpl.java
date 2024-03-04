package com.modsen.driverservice.service.impl;


import com.modsen.driverservice.dto.request.DriverRatingRequest;
import com.modsen.driverservice.dto.response.AverageDriverRatingResponse;
import com.modsen.driverservice.dto.response.DriverListRatingsResponse;
import com.modsen.driverservice.dto.response.DriverRatingResponse;
import com.modsen.driverservice.dto.response.PassengerResponse;
import com.modsen.driverservice.entity.Rating;
import com.modsen.driverservice.exception.NotFoundException;
import com.modsen.driverservice.repository.DriverRepository;
import com.modsen.driverservice.repository.RatingRepository;
import com.modsen.driverservice.service.PassengerService;
import com.modsen.driverservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.modsen.driverservice.util.SecurityUtil.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RatingServiceImpl implements RatingService {
    private final RatingRepository driverRatingRepository;
    private final DriverRepository driverRepository;
    private final ModelMapper modelMapper;
    private final PassengerService passengerService;

    private PassengerResponse getPassenger(UUID id) {
        return passengerService.getPassenger(id);
    }

    @Override
    public DriverRatingResponse rateDriver(DriverRatingRequest driverRatingRequest, UUID driverId, OAuth2User principal) {
        Rating newDriverRating = toEntity(driverRatingRequest);
        newDriverRating.setPassengerId(principal.getAttribute(ID_KEY));
        newDriverRating.setDriver(driverRepository.findByIdAndActiveIsTrue(driverId)
                .orElseThrow(() -> new NotFoundException(driverId)));
        log.info("Update rating for driver {}", driverId);
        return fromEntityToRatingResponse(driverRatingRepository.save(newDriverRating));
    }

    @Override
    @Transactional(readOnly = true)
    public DriverListRatingsResponse getRatingsByDriverId(UUID driverId) {
        validateDriverExists(driverId);
        List<DriverRatingResponse> driverRatings = driverRatingRepository.getRatingsByDriverId(driverId)
                .stream()
                .map(this::fromEntityToRatingResponse)
                .toList();
        log.info("Retrieving rating for driver {}", driverId);
        return DriverListRatingsResponse.builder()
                .driverRatings(driverRatings)
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public AverageDriverRatingResponse getAverageDriverRating(UUID driverId) {
        validateDriverExists(driverId);
        double averageRating = driverRatingRepository
                .getRatingsByDriverId(driverId)
                .stream()
                .mapToDouble(Rating::getScore)
                .average()
                .orElse(5.0);
        log.info("Retrieving average for driver {}", driverId);
        return AverageDriverRatingResponse.builder()
                .averageRating(Math.round(averageRating * 100.0) / 100.0)
                .driverId(driverId)
                .build();
    }

    private void validateDriverExists(UUID driverId) {
        driverRepository.findById(driverId)
                .orElseThrow(() -> new NotFoundException(driverId));
    }

    private Rating toEntity(DriverRatingRequest driverRatingRequest) {
        return modelMapper.map(driverRatingRequest, Rating.class);
    }

    private DriverRatingResponse fromEntityToRatingResponse(Rating driverRating) {
        DriverRatingResponse response = modelMapper.map(driverRating, DriverRatingResponse.class);
        response.setPassengerResponse(getPassenger(driverRating.getPassengerId()));
        return response;
    }
}