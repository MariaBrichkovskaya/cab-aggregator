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
import java.util.stream.Collectors;

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
            throw new AlreadyExistsException(String.format(DRIVER_WITH_PHONE_EXISTS_MESSAGE,request.getPhone()));
        }
        driverRepository.save(toEntity(request));
        log.info("Create driver with surname {}",request.getSurname());

    }


    @Override
    public DriverResponse findById(Long id) {
        Driver driver=driverRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        log.info("Retrieving driver by id {}", id);
        return toDto(driver);
    }

    @Override
    public DriversListResponse findAll(int page, int size, String sortingParam) {
        PageRequest pageRequest = getPageRequest(page, size, sortingParam);
        Page<Driver> driversPage = driverRepository.findAll(pageRequest);
        List<DriverResponse> drivers= driversPage.getContent().stream()
                .map(this::toDto).toList();
        return new DriversListResponse(drivers);
    }
    private PageRequest getPageRequest(int page, int size, String sortingParam) {
        if (page < 1 || size < 1) {
            log.error("Invalid page request");
            throw new InvalidRequestException(INVALID_PAGE_MESSAGE);
        }
        PageRequest pageRequest;
        if (sortingParam == null) {
            pageRequest = PageRequest.of(page-1, size,Sort.by("id"));
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
                throw new AlreadyExistsException(String.format(DRIVER_WITH_PHONE_EXISTS_MESSAGE,request.getPhone()));
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

    @Override
    public void changeStatus(Long id) {
        Driver driver=driverRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        if(driver.getStatus().equals(Status.AVAILABLE)){
            driver.setStatus(Status.UNAVAILABLE);
        } else {
            driver.setStatus(Status.AVAILABLE);
        }
        driverRepository.save(driver);
    }

    @Override
    public void editRating(Integer score,Long id) {
        Driver driver=driverRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        double rating=(driver.getRating()+score)/2;
        driver.setRating(rating);
        driverRepository.save(driver);
    }

    @Override
    public DriversListResponse findAvailableDrivers(int page,int size,String sortingParam) {
        PageRequest pageRequest = getPageRequest(page, size, sortingParam);
        Page<Driver> driversPage = driverRepository.findByStatus(Status.AVAILABLE,pageRequest);
        List<DriverResponse> drivers= driversPage.getContent().stream()
                .map(this::toDto).toList();
        return new DriversListResponse(drivers);
    }


}
