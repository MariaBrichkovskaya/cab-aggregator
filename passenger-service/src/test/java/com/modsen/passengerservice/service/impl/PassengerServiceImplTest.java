package com.modsen.passengerservice.service.impl;

import com.modsen.passengerservice.dto.request.PassengerRequest;
import com.modsen.passengerservice.dto.response.PassengerResponse;
import com.modsen.passengerservice.dto.response.PassengersListResponse;
import com.modsen.passengerservice.entity.Passenger;
import com.modsen.passengerservice.exception.AlreadyExistsException;
import com.modsen.passengerservice.exception.InvalidRequestException;
import com.modsen.passengerservice.exception.NotFoundException;
import com.modsen.passengerservice.repository.PassengerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static com.modsen.passengerservice.util.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class PassengerServiceImplTest {

    @Mock
    private PassengerRepository passengerRepository;
    @Mock
    private ModelMapper modelMapper;


    @InjectMocks
    private PassengerServiceImpl passengerService;
    @Mock
    private RatingServiceImpl ratingService;

    @Test
    void addPassengerWhenPassengerUnique() {
        PassengerResponse expected = getDefaultPassengerResponse();
        Passenger passengerToSave = getNotSavedPassenger();
        Passenger savedPassenger = getDefaultPassenger();
        PassengerRequest createRequest = getPassengerRequest();

        doReturn(false)
                .when(passengerRepository)
                .existsByEmail(DEFAULT_EMAIL);
        doReturn(false)
                .when(passengerRepository)
                .existsByPhone(DEFAULT_PHONE);
        doReturn(passengerToSave)
                .when(modelMapper)
                .map(createRequest, Passenger.class);
        doReturn(savedPassenger)
                .when(passengerRepository)
                .save(passengerToSave);
        doReturn(expected)
                .when(modelMapper)
                .map(savedPassenger, PassengerResponse.class);
        doReturn(getDefaultRating())
                .when(ratingService)
                .getAveragePassengerRating(DEFAULT_ID);
        PassengerResponse actual = passengerService.add(createRequest);

        verify(passengerRepository).existsByEmail(DEFAULT_EMAIL);
        verify(passengerRepository).existsByPhone(DEFAULT_PHONE);
        verify(passengerRepository).save(passengerToSave);
        verify(modelMapper).map(createRequest, Passenger.class);
        verify(modelMapper).map(savedPassenger, PassengerResponse.class);
        verify(ratingService).getAveragePassengerRating(DEFAULT_ID);

        assertThat(actual).isEqualTo(expected);

    }

    @Test
    void addPassengerWhenDataIsNotUnique() {
        PassengerRequest createRequest = getPassengerRequest();

        doReturn(true)
                .when(passengerRepository)
                .existsByEmail(DEFAULT_EMAIL);
        doReturn(false)
                .when(passengerRepository)
                .existsByPhone(DEFAULT_PHONE);

        assertThrows(
                AlreadyExistsException.class,
                () -> passengerService.add(createRequest)
        );

        verify(passengerRepository).existsByEmail(DEFAULT_EMAIL);
        verify(passengerRepository).existsByPhone(DEFAULT_PHONE);
    }

    @Test
    void addPassengerWhenPhoneIsNotUnique() {
        PassengerRequest createRequest = getPassengerRequest();

        doReturn(false)
                .when(passengerRepository)
                .existsByEmail(DEFAULT_EMAIL);
        doReturn(true)
                .when(passengerRepository)
                .existsByPhone(DEFAULT_PHONE);

        assertThrows(
                AlreadyExistsException.class,
                () -> passengerService.add(createRequest)
        );

        verify(passengerRepository).existsByEmail(DEFAULT_EMAIL);
        verify(passengerRepository).existsByPhone(DEFAULT_PHONE);
    }


    @Test
    void findByIdPassengerShouldExist() {
        Passenger retrievedPassenger = getDefaultPassenger();
        doReturn(Optional.of(retrievedPassenger))
                .when(passengerRepository)
                .findById(DEFAULT_ID);
        PassengerResponse expected = getDefaultPassengerResponse();
        doReturn(expected)
                .when(modelMapper)
                .map(retrievedPassenger, PassengerResponse.class);
        doReturn(getDefaultRating())
                .when(ratingService)
                .getAveragePassengerRating(DEFAULT_ID);
        PassengerResponse actual = passengerService.findById(DEFAULT_ID);
        verify(passengerRepository).findById(DEFAULT_ID);
        verify(modelMapper).map(retrievedPassenger, PassengerResponse.class);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findByIdPassengerNotFound() {
        doReturn(Optional.empty())
                .when(passengerRepository)
                .findById(DEFAULT_ID);

        assertThrows(
                NotFoundException.class,
                () -> passengerService.findById(DEFAULT_ID)
        );
        verify(passengerRepository).findById(DEFAULT_ID);
    }


    @Test
    void findAllWhenParamsInvalid() {
        assertThrows(
                InvalidRequestException.class,
                () -> passengerService.findAll(INVALID_PAGE, INVALID_SIZE, INVALID_ORDER_BY)
        );
    }

    @Test
    void updateWhenPassengerNotFound() {
        doReturn(Optional.empty())
                .when(passengerRepository)
                .findById(DEFAULT_ID);
        PassengerRequest passengerRequest = getPassengerRequest();
        assertThrows(
                NotFoundException.class,
                () -> passengerService.update(passengerRequest, DEFAULT_ID)
        );
        verify(passengerRepository).findById(DEFAULT_ID);
    }


    @Test
    void deleteWhenPassengerNotFound() {
        doReturn(false)
                .when(passengerRepository)
                .existsById(DEFAULT_ID);
        assertThrows(
                NotFoundException.class,
                () -> passengerService.delete(DEFAULT_ID)
        );
        verify(passengerRepository).existsById(DEFAULT_ID);
    }

    @Test
    void deleteWhenPassengerExists() {
        doReturn(true)
                .when(passengerRepository)
                .existsById(DEFAULT_ID);
        passengerService.delete(DEFAULT_ID);
        verify(passengerRepository).deleteById(DEFAULT_ID);
    }

    @Test
    void findAllWhenParamsValid() {

    }

    @Test
    void updateWhenPassengerDataIsNotUnique() {

    }
    @Test
    void updateWhenPassengerExistsAndDataIsUnique() {

    }


}