package com.modsen.driverservice.service.impl;

import com.modsen.driverservice.dto.request.DriverRequest;
import com.modsen.driverservice.dto.response.DriverResponse;
import com.modsen.driverservice.dto.response.DriversListResponse;
import com.modsen.driverservice.entity.Driver;
import com.modsen.driverservice.enums.Status;
import com.modsen.driverservice.exception.AlreadyExistsException;
import com.modsen.driverservice.exception.InvalidRequestException;
import com.modsen.driverservice.exception.NotFoundException;
import com.modsen.driverservice.repository.DriverRepository;
import com.modsen.driverservice.service.DriverService;
import com.modsen.driverservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.modsen.driverservice.util.Messages.*;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DriverServiceImpl implements DriverService {
    private final ModelMapper modelMapper;
    private final DriverRepository driverRepository;
    private final RatingService ratingService;


    private DriverResponse toDto(Driver driver) {
        return modelMapper.map(driver, DriverResponse.class);
    }

    private Driver toEntity(DriverRequest request) {
        return modelMapper.map(request, Driver.class);
    }

    @Override
    public DriverResponse add(DriverRequest request) {
        if (driverRepository.existsByPhone(request.getPhone())) {
            log.error("Driver with phone {} is exists", request.getPhone());
            throw new AlreadyExistsException(String.format(DRIVER_WITH_PHONE_EXISTS_MESSAGE, request.getPhone()));
        }
        log.info("Create driver with surname {}", request.getSurname());
        DriverResponse response = toDto(driverRepository.save(toEntity(request)));
        response.setRating(ratingService.getAverageDriverRating(response.getId()).getAverageRating());
        return response;
    }


    @Override
    @Transactional(readOnly = true)
    public DriverResponse findById(Long id) {
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        log.info("Retrieving driver by id {}", id);
        DriverResponse response = toDto(driver);
        response.setRating(ratingService.getAverageDriverRating(id).getAverageRating());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public DriversListResponse findAll(int page, int size, String sortingParam) {
        PageRequest pageRequest = getPageRequest(page, size, sortingParam);
        Page<Driver> driversPage = driverRepository.findAll(pageRequest);
        return getDriversListResponse(driversPage);
    }

    private DriversListResponse getDriversListResponse(Page<Driver> driversPage) {
        List<DriverResponse> drivers = driversPage.getContent().stream()
                .map(driver -> {
                    DriverResponse response = toDto(driver);
                    response.setRating(ratingService.getAverageDriverRating(driver.getId()).getAverageRating());
                    return response;
                }).toList();
        return DriversListResponse.builder().drivers(drivers).build();
    }

    private PageRequest getPageRequest(int page, int size, String sortingParam) {
        if (page < 1 || size < 1) {
            log.error("Invalid page request");
            throw new InvalidRequestException(INVALID_PAGE_MESSAGE);
        }
        PageRequest pageRequest;
        if (sortingParam == null) {
            pageRequest = PageRequest.of(page - 1, size, Sort.by("id"));
        } else {
            validateSortingParameter(sortingParam);
            pageRequest = PageRequest.of(page - 1, size, Sort.by(sortingParam));
        }

        return pageRequest;
    }

    private void validateSortingParameter(String sortingParam) {
        List<String> fieldNames = Arrays.stream(DriverResponse.class.getDeclaredFields())
                .map(Field::getName)
                .toList();

        if (!fieldNames.contains(sortingParam)) {
            String errorMessage = String.format(INVALID_SORTING_MESSAGE, fieldNames);
            throw new InvalidRequestException(errorMessage);
        }
    }

    @Override
    public DriverResponse update(DriverRequest request, Long id) {
        if (driverRepository.findById(id).isEmpty()) {
            log.error("Driver with id {} was not found", id);
            throw new NotFoundException(id);
        }
        Driver driver = toEntity(request);
        if (!Objects.equals(request.getPhone(), driver.getPhone())) {
            if (driverRepository.existsByPhone(request.getPhone())) {
                log.error("Driver with phone {} is exists", request.getPhone());
                throw new AlreadyExistsException(String.format(DRIVER_WITH_PHONE_EXISTS_MESSAGE, request.getPhone()));
            }
        }
        driver.setId(id);
        log.info("Update driver with id {}", id);
        DriverResponse response = toDto(driverRepository.save(driver));
        response.setRating(ratingService.getAverageDriverRating(driver.getId()).getAverageRating());
        return response;
    }


    @Override
    public void delete(Long id) {
        if (driverRepository.findById(id).isEmpty()) {
            log.error("Driver with id {} was not found", id);
            throw new NotFoundException(id);
        }
        driverRepository.deleteById(id);
        log.info("Delete driver with id {}", id);

    }

    @Override
    public void changeStatus(Long id) {
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        if (driver.getStatus().equals(Status.AVAILABLE)) {
            driver.setStatus(Status.UNAVAILABLE);
        } else {
            driver.setStatus(Status.AVAILABLE);
        }
        driverRepository.save(driver);
    }


    @Override
    @Transactional(readOnly = true)
    public DriversListResponse findAvailableDrivers(int page, int size, String sortingParam) {
        PageRequest pageRequest = getPageRequest(page, size, sortingParam);
        Page<Driver> driversPage = driverRepository.findByStatus(Status.AVAILABLE, pageRequest);
        return getDriversListResponse(driversPage);
    }


}
