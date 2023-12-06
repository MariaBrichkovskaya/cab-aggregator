package com.modsen.driverservice.service.impl;

import com.modsen.driverservice.dto.request.DriverRequest;
import com.modsen.driverservice.dto.response.DriverResponse;
import com.modsen.driverservice.entity.Driver;
import com.modsen.driverservice.exception.AlreadyExistsException;
import com.modsen.driverservice.exception.InvalidRequestException;
import com.modsen.driverservice.exception.NotFoundException;
import com.modsen.driverservice.repository.DriverRepository;
import com.modsen.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.lang.reflect.Field;
import java.util.Objects;

import static com.modsen.driverservice.util.Messages.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService {
    private final ModelMapper modelMapper;
    private final DriverRepository driverRepository;


    private DriverResponse toDto(Driver driver){
        return modelMapper.map(driver,DriverResponse.class);
    }

    private Driver toEntity(DriverRequest request){
        return modelMapper.map(request,Driver.class);
    }

    @Override
    public void add(DriverRequest request) {
        if (driverRepository.existsByPhone(request.getPhone())) {
            log.error("Driver with phone {} is exists", request.getPhone());
            throw new AlreadyExistsException(DRIVER_WITH_PHONE_EXISTS_MESSAGE);
        }
        driverRepository.save(toEntity(request));
        log.info("Create driver with surname {}",request.getSurname());

    }


    @Override
    public DriverResponse findById(Long id) {
        Driver passenger=driverRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        log.info("Retrieving driver by id {}", id);
        return toDto(passenger);
    }

    @Override
    public List<DriverResponse> findAll(int page, int size, String sortingParam) {
        PageRequest pageRequest = getPageRequest(page, size, sortingParam);
        Page<Driver> driversPage = driverRepository.findAll(pageRequest);
        return driversPage.getContent().stream()
                .map(this::toDto).toList();
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
        List<String> fieldNames = Arrays.stream(DriverResponse.class.getDeclaredFields())
                .map(Field::getName)
                .toList();

        if (!fieldNames.contains(sortingParam)) {
            String errorMessage = String.format(INVALID_SORTING_MESSAGE, fieldNames);
            throw new InvalidRequestException(errorMessage);
        }
    }

    @Override
    public void update(DriverRequest request, Long id) {
        if(driverRepository.findById(id).isEmpty()) {
            log.error("Driver with id {} was not found", id);
            throw new NotFoundException(id);
        }
        Driver driver = toEntity(request);
        if (!Objects.equals(request.getPhone(), driver.getPhone())) {
            if (driverRepository.existsByPhone(request.getPhone())) {
                log.error("Driver with phone {} is exists", request.getPhone());
                throw new AlreadyExistsException(DRIVER_WITH_PHONE_EXISTS_MESSAGE);
            }
        }
        driver.setId(id);
        driverRepository.save(driver);
        log.info("Update driver with id {}", id);
    }


    @Override
    public void delete(Long id) {
        if(driverRepository.findById(id).isEmpty()) {
            log.error("Driver with id {} was not found", id);
            throw new NotFoundException(id);
        }
        driverRepository.deleteById(id);
        log.info("Delete driver with id {}", id);

    }
}
