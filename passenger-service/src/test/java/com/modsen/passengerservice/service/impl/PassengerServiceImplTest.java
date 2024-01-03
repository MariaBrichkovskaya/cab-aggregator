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
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Optional;

import static com.modsen.passengerservice.util.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

        Page<Passenger> passengerPage = new PageImpl<>(Arrays.asList(
                getDefaultPassenger(),
                getSecondPassenger()
        ));

        when(passengerRepository.findAll(any(PageRequest.class))).thenReturn(passengerPage);
        doReturn(getDefaultPassengerResponse()).when(modelMapper).map(any(Passenger.class), eq(PassengerResponse.class));
        when(ratingService.getAveragePassengerRating(any(Long.class))).thenReturn(getDefaultRating());

        PassengersListResponse response = passengerService.findAll(VALID_PAGE, VALID_SIZE, VALID_ORDER_BY);

        assertNotNull(response);
        assertEquals(2, response.getPassengers().size());
        assertEquals(1, response.getPassengers().get(0).getId());
        assertEquals(DEFAULT_NAME, response.getPassengers().get(0).getName());

    }

    @Test
    void updateWhenPassengerDataIsNotUnique() {
        Passenger passengerToUpdate = getUpdatePassenger();
        PassengerRequest request = getPassengerRequest();
        doReturn(Optional.of(passengerToUpdate)).when(passengerRepository).findById(DEFAULT_ID);
        doReturn(true).when(passengerRepository).existsByPhone(request.getPhone());
        doReturn(true).when(passengerRepository).existsByEmail(request.getEmail());
        assertThrows(
                AlreadyExistsException.class,
                () -> passengerService.update(request, DEFAULT_ID)
        );
        verify(passengerRepository).findById(DEFAULT_ID);
        verify(passengerRepository).existsByEmail(request.getEmail());
        verify(passengerRepository).existsByPhone(request.getPhone());
    }

    @Test
    void updateWhenPassengerExistsAndDataIsUnique() {

        Passenger passengerToUpdate = getUpdatePassenger();
        PassengerRequest request = getPassengerRequest();
        PassengerResponse response = getDefaultPassengerResponse();

        when(passengerRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(passengerToUpdate));
        when(passengerRepository.existsByPhone(request.getPhone())).thenReturn(false);
        when(passengerRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(modelMapper.map(request, Passenger.class)).thenReturn(passengerToUpdate);
        when(passengerRepository.save(passengerToUpdate)).thenReturn(passengerToUpdate);
        when(modelMapper.map(passengerToUpdate, PassengerResponse.class)).thenReturn(response);
        when(ratingService.getAveragePassengerRating(DEFAULT_ID)).thenReturn(getDefaultRating());

        PassengerResponse result = passengerService.update(request, DEFAULT_ID);

        verify(passengerRepository).findById(DEFAULT_ID);
        verify(passengerRepository).existsByEmail(request.getEmail());
        verify(passengerRepository).existsByPhone(request.getPhone());
        verify(modelMapper).map(request, Passenger.class);
        verify(passengerRepository).save(passengerToUpdate);
        verify(modelMapper).map(passengerToUpdate, PassengerResponse.class);
        verify(ratingService).getAveragePassengerRating(DEFAULT_ID);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(request.getEmail());
    }


}