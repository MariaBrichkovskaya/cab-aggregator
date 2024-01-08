package com.modsen.driverservice.service.impl;

import com.modsen.driverservice.dto.request.DriverForRideRequest;
import com.modsen.driverservice.dto.request.DriverRequest;
import com.modsen.driverservice.dto.request.RideRequest;
import com.modsen.driverservice.dto.response.DriverResponse;
import com.modsen.driverservice.dto.response.DriversListResponse;
import com.modsen.driverservice.entity.Driver;
import com.modsen.driverservice.enums.Status;
import com.modsen.driverservice.exception.AlreadyExistsException;
import com.modsen.driverservice.exception.InvalidRequestException;
import com.modsen.driverservice.exception.NotFoundException;
import com.modsen.driverservice.kafka.DriverProducer;
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
import java.util.List;
import java.lang.reflect.Field;
import java.util.Objects;

import static com.modsen.driverservice.util.Messages.*;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DriverServiceImpl implements DriverService {
    private final ModelMapper modelMapper;
    private final DriverRepository driverRepository;
    private final RatingService ratingService;
    private final DriverProducer driverProducer;

    private DriverResponse fromEntityToDriverResponse(Driver driver) {
        DriverResponse response = modelMapper.map(driver, DriverResponse.class);
        response.setRating(ratingService.getAverageDriverRating(response.getId()).getAverageRating());

        return response;
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

        return fromEntityToDriverResponse(driverRepository.save(toEntity(request)));
    }


    @Override
    @Transactional(readOnly = true)
    public DriverResponse findById(Long id) {
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        log.info("Retrieving driver by id {}", id);

        return fromEntityToDriverResponse(driver);
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
                .map(this::fromEntityToDriverResponse)
                .toList();

        return DriversListResponse.builder()
                .drivers(drivers)
                .build();
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
        checkDriverUnique(request, id);
        Driver updatedDriver = toEntity(request);
        updatedDriver.setId(id);
        log.info("Update driver with id {}", id);

        return fromEntityToDriverResponse(driverRepository.save(updatedDriver));
    }

    private void checkDriverUnique(DriverRequest request, Long id) {
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        if (!Objects.equals(request.getPhone(), driver.getPhone())) {
            if (driverRepository.existsByPhone(request.getPhone())) {
                log.error("Driver with phone {} is exists", request.getPhone());
                throw new AlreadyExistsException(String.format(DRIVER_WITH_PHONE_EXISTS_MESSAGE, request.getPhone()));
            }
        }
    }


    @Override
    public void delete(Long id) {
        if (!driverRepository.existsById(id)) {
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

    @Override
    public void findDriverForRide(RideRequest request) {
        List<DriverResponse> drivers = findAvailableDrivers(1, 1, "id").getDrivers();
        if (!drivers.isEmpty()) {
            DriverForRideRequest driver = DriverForRideRequest.builder()
                    .driverId(drivers.get(0).getId())
                    .rideId(request.getId())
                    .build();
            driverProducer.sendMessage(driver);
        }

    }


}
