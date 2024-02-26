package com.modsen.driverservice.service.impl;

import com.modsen.driverservice.dto.request.DriverForRideRequest;
import com.modsen.driverservice.dto.request.DriverRequest;
import com.modsen.driverservice.dto.request.RideRequest;
import com.modsen.driverservice.dto.response.DriverResponse;
import com.modsen.driverservice.dto.response.DriversListResponse;
import com.modsen.driverservice.dto.response.MessageResponse;
import com.modsen.driverservice.entity.Driver;
import com.modsen.driverservice.enums.Status;
import com.modsen.driverservice.exception.AlreadyExistsException;
import com.modsen.driverservice.exception.DriverIsUnavailableException;
import com.modsen.driverservice.exception.InvalidRequestException;
import com.modsen.driverservice.exception.NotFoundException;
import com.modsen.driverservice.kafka.DriverProducer;
import com.modsen.driverservice.mapper.DriverMapper;
import com.modsen.driverservice.repository.DriverRepository;
import com.modsen.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.modsen.driverservice.util.Messages.*;
import static com.modsen.driverservice.util.SecurityUtil.*;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DriverServiceImpl implements DriverService {
    private final DriverMapper driverMapper;
    private final DriverRepository driverRepository;
    private final DriverProducer driverProducer;


    @Override
    public DriverResponse add(OAuth2User principal) {
        DriverRequest request = createRequestFromPrincipal(principal);
        checkExistence(request.getPhone());
        Driver driver = driverMapper.toEntity(request);
        driver.setId(principal.getAttribute(ID_KEY));
        driverRepository.save(driver);
        log.info("Create driver with surname {}", request.getSurname());

        return driverMapper.toDriverResponse(driver);
    }


    @Override
    @Transactional(readOnly = true)
    public DriverResponse findById(UUID id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("driver with id {} is not found", id);
                    return new NotFoundException(id);
                });
        log.info("Retrieving driver by id {}", id);

        return driverMapper.toDriverResponse(driver);
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
                .map(driverMapper::toDriverResponse)
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
            log.error(INVALID_SORTING_MESSAGE);
            String errorMessage = String.format(INVALID_SORTING_MESSAGE, fieldNames);
            throw new InvalidRequestException(errorMessage);
        }
    }

    @Override
    public DriverResponse update(DriverRequest request, UUID id) {
        checkDriverUnique(request, id);
        Driver updatedDriver = driverMapper.toEntity(request);
        updatedDriver.setId(id);
        log.info("Update driver with id {}", id);

        return driverMapper.toDriverResponse(driverRepository.save(updatedDriver));
    }

    private void checkDriverUnique(DriverRequest request, UUID id) {
        Driver driver = driverRepository.findByIdAndActiveIsTrue(id)
                .orElseThrow(() -> {
                            log.error("driver with id {} is not found", id);
                            return new NotFoundException(id);
                        }
                );
        if (!Objects.equals(request.getPhone(), driver.getPhone())) {
            checkExistence(request.getPhone());
        }
    }


    @Override
    public MessageResponse delete(UUID id) {
        Driver driver = driverRepository.findByIdAndActiveIsTrue(id).orElseThrow(() -> new NotFoundException(id));
        if (driver.getStatus().equals(Status.UNAVAILABLE)) {
            log.error(UNAVAILABLE_DRIVER_MESSAGE.formatted(id));
            throw new DriverIsUnavailableException(String.format(UNAVAILABLE_DRIVER_MESSAGE, id));
        }
        driver.setActive(false);
        driverRepository.save(driver);
        log.info("Delete driver with id {}", id);
        return MessageResponse.builder()
                .message(String.format(DELETE_DRIVER_MESSAGE, id))
                .build();
    }

    @Override
    public MessageResponse changeStatus(UUID id) {
        Driver driver = driverRepository.findByIdAndActiveIsTrue(id)
                .orElseThrow(() -> {
                            log.error("driver with id {} is not found", id);
                            return new NotFoundException(id);
                        }
                );
        if (driver.getStatus().equals(Status.AVAILABLE)) {
            driver.setStatus(Status.UNAVAILABLE);
        } else {
            driver.setStatus(Status.AVAILABLE);
            driverProducer.sendMessage(DriverForRideRequest.builder()
                    .driverId(driver.getId())
                    .build()
            );
        }
        log.info(String.format(EDIT_DRIVER_STATUS_MESSAGE, id));
        driverRepository.save(driver);
        return MessageResponse.builder()
                .message(String.format(EDIT_DRIVER_STATUS_MESSAGE, id))
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public DriversListResponse findAvailableDrivers(int page, int size, String sortingParam) {
        PageRequest pageRequest = getPageRequest(page, size, sortingParam);
        Page<Driver> driversPage = driverRepository.findByStatusAndActiveIsTrue(Status.AVAILABLE, pageRequest);
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
            log.info("Retrieve drivers for ride");
            driverProducer.sendMessage(driver);
        }

    }

    private DriverRequest createRequestFromPrincipal(OAuth2User principal) {
        return DriverRequest.builder()
                .name(principal.getAttribute(NAME_KEY))
                .surname(principal.getAttribute(SURNAME_KEY))
                .phone(principal.getAttribute(PHONE_KEY))
                .build();
    }

    private void checkExistence(String phone) {
        if (driverRepository.existsByPhoneAndActiveIsTrue(phone)) {
            log.error("Driver with phone {} is exists", phone);
            throw new AlreadyExistsException(String.format(DRIVER_WITH_PHONE_EXISTS_MESSAGE, phone));
        }
    }

}
