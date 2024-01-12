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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.modsen.passengerservice.util.TestUtils.*;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RatingServiceImplTest {
    @Mock
    private PassengerRepository passengerRepository;
    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private DriverFeignClient driverFeignClient;
    @InjectMocks
    private RatingServiceImpl ratingService;

    @Test
    void rateWhenPassengerNotFound() {
        PassengerRatingRequest request = getDefaultPassengerRatingRequest();
        Rating rating = getDefaultPassengerRating();
        doReturn(rating).when(modelMapper).map(request, Rating.class);
        doReturn(Optional.empty())
                .when(passengerRepository)
                .findById(rating.getPassenger().getId());
        assertThrows(
                NotFoundException.class,
                () -> ratingService.ratePassenger(request, rating.getPassenger().getId())
        );
    }

    @Test
    void rateWhenPassengerExists() {
        PassengerRatingRequest request = getDefaultPassengerRatingRequest();
        Rating ratingToSave = getDefaultPassengerRating();
        PassengerRatingResponse response = getDefaultPassengerRatingResponse();
        Rating savedRating = getSavedPassengerRating();
        DriverResponse driverResponse = getDefaultDriverResponse();

        doReturn(ratingToSave)
                .when(modelMapper)
                .map(request, Rating.class);
        doReturn(Optional.of(getDefaultPassenger())).when(passengerRepository).findById(DEFAULT_ID);
        doReturn(savedRating)
                .when(ratingRepository)
                .save(ratingToSave);
        doReturn(response).when(modelMapper).map(savedRating, PassengerRatingResponse.class);
        doReturn(driverResponse).when(driverFeignClient).getDriver(request.getDriverId());
        PassengerRatingResponse expected = ratingService.ratePassenger(request, DEFAULT_ID);

        assertNotNull(expected);
        verify(passengerRepository).findById(DEFAULT_ID);
        verify(ratingRepository).save(ratingToSave);
        verify(modelMapper).map(savedRating, PassengerRatingResponse.class);
        verify(driverFeignClient).getDriver(request.getDriverId());
    }

    @Test
    void getRatingsByPassengerIdWhenPassengerNotFound() {
        doReturn(Optional.empty())
                .when(passengerRepository)
                .findById(DEFAULT_ID);
        assertThrows(
                NotFoundException.class,
                () -> ratingService.getRatingsByPassengerId(DEFAULT_ID)
        );
    }

    @Test
    void getRatingsByPassengerIdWhenPassengerExists() {
        List<Rating> ratings = Arrays.asList(
                getSavedPassengerRating(),
                getNewSavedPassengerRating()
        );

        when(passengerRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(getDefaultPassenger()));
        when(ratingRepository.getRatingsByPassengerId(DEFAULT_ID)).thenReturn(ratings);
        doReturn(getDefaultPassengerRatingResponse()).when(modelMapper).map(any(Rating.class), eq(PassengerRatingResponse.class));
        when(driverFeignClient.getDriver(DEFAULT_ID)).thenReturn(getDefaultDriverResponse());

        PassengerListRatingsResponse response = ratingService.getRatingsByPassengerId(DEFAULT_ID);

        verify(passengerRepository).findById(DEFAULT_ID);
        verify(ratingRepository).getRatingsByPassengerId(DEFAULT_ID);
        verify(driverFeignClient, times(ratings.size())).getDriver(DEFAULT_ID);

        assertNotNull(response);
        assertEquals(ratings.size(), response.getPassengerRatings().size());
        assertEquals(DEFAULT_SCORE, response.getPassengerRatings().get(0).getScore());

    }

    @Test
    void getAveragePassengerRatingWhenPassengerNotFound() {
        doReturn(Optional.empty())
                .when(passengerRepository)
                .findById(DEFAULT_ID);
        assertThrows(
                NotFoundException.class,
                () -> ratingService.getAveragePassengerRating(DEFAULT_ID)
        );
    }

    @Test
    void getAveragePassengerRatingWhenPassengerExists() {
        List<Rating> ratings = Arrays.asList(
                getSavedPassengerRating(),
                getNewSavedPassengerRating()
        );

        when(ratingRepository.getRatingsByPassengerId(DEFAULT_ID)).thenReturn(ratings);
        doReturn(Optional.of(getDefaultPassenger())).when(passengerRepository).findById(DEFAULT_ID);
        AveragePassengerRatingResponse response = ratingService.getAveragePassengerRating(DEFAULT_ID);

        verify(ratingRepository).getRatingsByPassengerId(DEFAULT_ID);
        assertNotNull(response);
        assertEquals(DEFAULT_AVERAGE_RATING, response.getAverageRating());

    }
}