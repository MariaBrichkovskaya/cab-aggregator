package com.modsen.passengerservice.service.impl;

import com.modsen.passengerservice.dto.request.PassengerRequest;
import com.modsen.passengerservice.dto.response.MessageResponse;
import com.modsen.passengerservice.dto.response.PassengerResponse;
import com.modsen.passengerservice.dto.response.PassengersListResponse;
import com.modsen.passengerservice.entity.Passenger;
import com.modsen.passengerservice.exception.AlreadyExistsException;
import com.modsen.passengerservice.exception.InvalidRequestException;
import com.modsen.passengerservice.exception.NotFoundException;
import com.modsen.passengerservice.repository.PassengerRepository;
import com.modsen.passengerservice.service.PassengerService;
import com.modsen.passengerservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;

import java.util.*;

import static com.modsen.passengerservice.util.Messages.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PassengerServiceImpl implements PassengerService {
    private final ModelMapper modelMapper;
    private final PassengerRepository passengerRepository;
    private final RatingService ratingService;

    private PassengerResponse fromEntityToPassengerResponse(Passenger passenger) {
        PassengerResponse response = modelMapper.map(passenger, PassengerResponse.class);
        response.setRating(ratingService.getAveragePassengerRating(passenger.getId()).getAverageRating());
        return response;
    }

    private Passenger toEntity(PassengerRequest request) {
        return modelMapper.map(request, Passenger.class);
    }

    @Override
    public PassengerResponse add(PassengerRequest request) {
        checkCreateDataIsUnique(request);
        Passenger passenger = passengerRepository.save(toEntity(request));
        log.info("Create passenger with surname {}", request.getSurname());

        return fromEntityToPassengerResponse(passenger);
    }

    @Override
    @Transactional(readOnly = true)
    public PassengerResponse findById(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        log.info("Retrieving passenger by id {}", id);

        return fromEntityToPassengerResponse(passenger);
    }

    @Override
    @Transactional(readOnly = true)
    public PassengersListResponse findAll(int page, int size, String sortingParam) {
        PageRequest pageRequest = getPageRequest(page, size, sortingParam);
        Page<Passenger> passengersPage = passengerRepository.findAll(pageRequest);
        List<PassengerResponse> passengers = passengersPage.getContent().stream()
                .map(this::fromEntityToPassengerResponse)
                .toList();

        return PassengersListResponse.builder()
                .passengers(passengers)
                .build();
    }


    private PageRequest getPageRequest(int page, int size, String sortingParam) {
        if (page < 1 || size < 1) {
            log.error("Invalid page request");
            throw new InvalidRequestException(INVALID_PAGE_MESSAGE);
        }
        PageRequest pageRequest;
        if (sortingParam == null) {
            pageRequest = PageRequest.of(page - 1, size);
        } else {
            validateSortingParameter(sortingParam);
            pageRequest = PageRequest.of(page - 1, size, Sort.by(sortingParam));
        }

        return pageRequest;
    }

    private void validateSortingParameter(String sortingParam) {
        List<String> fieldNames = Arrays.stream(PassengerResponse.class.getDeclaredFields())
                .map(Field::getName)
                .toList();

        if (!fieldNames.contains(sortingParam)) {
            String errorMessage = String.format(INVALID_SORTING_MESSAGE, fieldNames);
            throw new InvalidRequestException(errorMessage);
        }
    }


    @Override
    public PassengerResponse update(PassengerRequest request, Long id) {
        Passenger passengerToUpdate = passengerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        checkUpdateDataIsUnique(request, passengerToUpdate);
        Passenger passenger = toEntity(request);
        passenger.setId(id);
        log.info("Update passenger with id {}", id);

        return fromEntityToPassengerResponse(passengerRepository.save(passenger));
    }

    @Override

    public MessageResponse delete(Long id) {
        if (!passengerRepository.existsById(id)) {
            log.error("Passenger with id {} was not found", id);
            throw new NotFoundException(id);
        }
        passengerRepository.deleteById(id);
        log.info("Delete passenger with id {}", id);
        String message = "Deleting passenger with id " + id;

        return MessageResponse.builder()
                .message(message)
                .build();
    }


    private void checkEmailIsUnique(String email, Map<String, String> errors) {
        if (passengerRepository.existsByEmail(email)) {
            log.error("Passenger with email {} is exists", email);
            errors.put(
                    "email",
                    String.format(PASSENGER_WITH_EMAIL_EXISTS_MESSAGE, email)
            );
        }
    }

    private void checkPhoneIsUnique(String phone, Map<String, String> errors) {
        if (passengerRepository.existsByPhone(phone)) {
            log.error("Passenger with phone {} is exists", phone);
            errors.put(
                    "phone",
                    String.format(PASSENGER_WITH_PHONE_EXISTS_MESSAGE, phone)
            );
        }
    }

    private void checkCreateDataIsUnique(PassengerRequest request) {
        var errors = new HashMap<String, String>();

        checkEmailIsUnique(request.getEmail(), errors);
        checkPhoneIsUnique(request.getPhone(), errors);

        if (!errors.isEmpty()) {
            throw new AlreadyExistsException(errors);
        }
    }

    private void checkUpdateDataIsUnique(PassengerRequest request, Passenger passenger) {
        var errors = new HashMap<String, String>();
        if (!Objects.equals(request.getPhone(), passenger.getPhone())) {
            checkPhoneIsUnique(request.getPhone(), errors);
        }
        if (!Objects.equals(request.getEmail(), passenger.getEmail())) {
            checkEmailIsUnique(request.getEmail(), errors);
        }
        if (!errors.isEmpty()) {
            throw new AlreadyExistsException(errors);
        }
    }
}
