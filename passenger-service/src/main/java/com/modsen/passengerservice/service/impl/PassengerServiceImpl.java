package com.modsen.passengerservice.service.impl;

import com.modsen.passengerservice.dto.request.PassengerRequest;
import com.modsen.passengerservice.dto.request.PassengerRatingRequest;
import com.modsen.passengerservice.dto.response.PassengerResponse;
import com.modsen.passengerservice.dto.response.PassengersListResponse;
import com.modsen.passengerservice.entity.Passenger;
import com.modsen.passengerservice.exception.AlreadyExistsException;
import com.modsen.passengerservice.exception.InvalidRequestException;
import com.modsen.passengerservice.exception.NotFoundException;
import com.modsen.passengerservice.repository.PassengerRepository;
import com.modsen.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.lang.reflect.Field;

import java.util.*;

import static com.modsen.passengerservice.util.Messages.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerServiceImpl implements PassengerService {
    private final ModelMapper modelMapper;
    private final PassengerRepository passengerRepository;

    private PassengerResponse toDto(Passenger passenger){
        return modelMapper.map(passenger,PassengerResponse.class);
    }

    private Passenger toEntity(PassengerRequest request){
        return modelMapper.map(request,Passenger.class);
    }

    @Override
    public void add(PassengerRequest request) {
        checkCreateDataIsUnique(request);
        passengerRepository.save(toEntity(request));
        log.info("Create passenger with surname {}",request.getSurname());
    }

    @Override
    public PassengerResponse findById(Long id) {
        Passenger passenger=passengerRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        log.info("Retrieving passenger by id {}", id);
        return toDto(passenger);

    }

    @Override
    public PassengersListResponse findAll(int page, int size, String sortingParam) {
        PageRequest pageRequest = getPageRequest(page, size, sortingParam);
        Page<Passenger> passengersPage = passengerRepository.findAll(pageRequest);
        List<PassengerResponse> passengers= passengersPage.getContent().stream()
                .map(this::toDto).toList();
        return PassengersListResponse.builder().passengers(passengers).build();
    }


    private PageRequest getPageRequest(int page, int size, String sortingParam) {
        if (page < 1 || size < 1) {
            log.error("Invalid page request");
            throw new InvalidRequestException(INVALID_PAGE_MESSAGE);
        }
        PageRequest pageRequest;
        if (sortingParam == null) {
            pageRequest = PageRequest.of(page-1, size);
        } else {
            validateSortingParameter(sortingParam);
            pageRequest = PageRequest.of(page-1, size, Sort.by(sortingParam));
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
    public void update(PassengerRequest request, Long id) {
        if(passengerRepository.findById(id).isEmpty()) {
            log.error("Passenger with id {} was not found", id);
            throw new NotFoundException(id);
        }
        Passenger passenger = toEntity(request);
        checkUpdateDataIsUnique(request,passenger);
        passenger.setId(id);
        passengerRepository.save(passenger);
        log.info("Update passenger with id {}", id);
    }

    @Override
    public void delete(Long id) {
        if(passengerRepository.findById(id).isEmpty()) {
            log.error("Passenger with id {} was not found", id);
            throw new NotFoundException(id);
        }
        passengerRepository.deleteById(id);
        log.info("Delete passenger with id {}", id);
    }


    private void checkEmailIsUnique(String email, Map<String, String> errors) {
        if (passengerRepository.existsByEmail(email)) {
            log.error("Passenger with email {} is exists",email);
            errors.put(
                    "email",
                    String.format(PASSENGER_WITH_EMAIL_EXISTS_MESSAGE, email)
            );
        }
    }

    private void checkPhoneIsUnique(String phone, Map<String, String> errors) {
        if (passengerRepository.existsByPhone(phone)) {
            log.error("Passenger with phone {} is exists",phone);
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
