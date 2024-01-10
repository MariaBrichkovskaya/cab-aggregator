package com.modsen.driverservice.service.impl;

import com.modsen.driverservice.client.PassengerFeignClient;
import com.modsen.driverservice.dto.request.DriverRatingRequest;
import com.modsen.driverservice.dto.response.AverageDriverRatingResponse;
import com.modsen.driverservice.dto.response.DriverListRatingsResponse;
import com.modsen.driverservice.dto.response.DriverRatingResponse;
import com.modsen.driverservice.dto.response.PassengerResponse;
import com.modsen.driverservice.entity.Rating;
import com.modsen.driverservice.exception.NotFoundException;
import com.modsen.driverservice.repository.DriverRepository;
import com.modsen.driverservice.repository.RatingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.modsen.driverservice.util.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceImplTest {

    @Mock
    private DriverRepository driverRepository;
    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private PassengerFeignClient passengerFeignClient;
    @InjectMocks
    private RatingServiceImpl ratingService;

    @Test
    void rateWhenDriverNotFound() {
        DriverRatingRequest request = getDefaultDriverRatingRequest();
        Rating rating = getDefaultDriverRating();
        doReturn(rating).when(modelMapper).map(request, Rating.class);
        doReturn(Optional.empty())
                .when(driverRepository)
                .findById(rating.getDriver().getId());
        assertThrows(
                NotFoundException.class,
                () -> ratingService.rateDriver(request, rating.getDriver().getId())
        );
    }

    @Test
    void rateWhenDriverExists() {
        DriverRatingRequest request = getDefaultDriverRatingRequest();
        Rating ratingToSave = getDefaultDriverRating();
        DriverRatingResponse response = getDefaultDriverRatingResponse();
        Rating savedRating = getSavedDriverRating();
        PassengerResponse passengerResponse = getDefaultPassengerResponse();

        doReturn(ratingToSave)
                .when(modelMapper)
                .map(request, Rating.class);
        doReturn(Optional.of(getDefaultDriver())).when(driverRepository).findById(DEFAULT_ID);
        doReturn(savedRating)
                .when(ratingRepository)
                .save(ratingToSave);
        doReturn(response).when(modelMapper).map(savedRating, DriverRatingResponse.class);
        doReturn(passengerResponse).when(passengerFeignClient).getPassenger(request.getPassengerId());
        DriverRatingResponse expected = ratingService.rateDriver(request, DEFAULT_ID);

        assertNotNull(expected);
        verify(driverRepository).findById(DEFAULT_ID);
        verify(ratingRepository).save(ratingToSave);
        verify(modelMapper).map(savedRating, DriverRatingResponse.class);
        verify(passengerFeignClient).getPassenger(request.getPassengerId());
    }

    @Test
    void getRatingsByDriverIdWhenDriverNotFound() {
        doReturn(Optional.empty())
                .when(driverRepository)
                .findById(DEFAULT_ID);
        assertThrows(
                NotFoundException.class,
                () -> ratingService.getRatingsByDriverId(DEFAULT_ID)
        );
    }

    @Test
    void getRatingsByDriverIdWhenDriverExists() {
        List<Rating> ratings = Arrays.asList(
                getSavedDriverRating(),
                getNewSavedDriverRating()
        );

        when(driverRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(getDefaultDriver()));
        when(ratingRepository.getRatingsByDriverId(DEFAULT_ID)).thenReturn(ratings);
        doReturn(getDefaultDriverRatingResponse()).when(modelMapper).map(any(Rating.class), eq(DriverRatingResponse.class));
        when(passengerFeignClient.getPassenger(DEFAULT_ID)).thenReturn(getDefaultPassengerResponse());

        DriverListRatingsResponse response = ratingService.getRatingsByDriverId(DEFAULT_ID);

        verify(driverRepository).findById(DEFAULT_ID);
        verify(ratingRepository).getRatingsByDriverId(DEFAULT_ID);
        verify(passengerFeignClient, times(ratings.size())).getPassenger(DEFAULT_ID);

        assertNotNull(response);
        assertEquals(ratings.size(), response.getDriverRatings().size());
        assertEquals(DEFAULT_SCORE, response.getDriverRatings().get(0).getScore());

    }

    @Test
    void getAverageDriverRatingWhenDriverNotFound() {
        doReturn(Optional.empty())
                .when(driverRepository)
                .findById(DEFAULT_ID);
        assertThrows(
                NotFoundException.class,
                () -> ratingService.getAverageDriverRating(DEFAULT_ID)
        );
    }

    @Test
    void getAverageDriverRatingWhenDriverExists() {
        List<Rating> ratings = Arrays.asList(
                getSavedDriverRating(),
                getNewSavedDriverRating()
        );

        when(ratingRepository.getRatingsByDriverId(DEFAULT_ID)).thenReturn(ratings);
        doReturn(Optional.of(getDefaultDriver())).when(driverRepository).findById(DEFAULT_ID);
        AverageDriverRatingResponse response = ratingService.getAverageDriverRating(DEFAULT_ID);

        verify(ratingRepository).getRatingsByDriverId(DEFAULT_ID);
        assertNotNull(response);
        assertEquals(AVERAGE_RATING, response.getAverageRating());

    }
}