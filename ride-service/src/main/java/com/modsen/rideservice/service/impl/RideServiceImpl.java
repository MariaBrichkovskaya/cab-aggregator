package com.modsen.rideservice.service.impl;

import com.modsen.rideservice.client.DriverFeignClient;
import com.modsen.rideservice.dto.request.CreateRideRequest;
import com.modsen.rideservice.dto.request.StatusRequest;
import com.modsen.rideservice.dto.request.UpdateRideRequest;
import com.modsen.rideservice.dto.response.*;
import com.modsen.rideservice.entity.Ride;
import com.modsen.rideservice.enums.Status;
import com.modsen.rideservice.exception.InvalidRequestException;
import com.modsen.rideservice.exception.NotFoundException;
import com.modsen.rideservice.repository.RideRepository;
import com.modsen.rideservice.service.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.modsen.rideservice.util.Messages.*;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RideServiceImpl implements RideService {
    private final RideRepository rideRepository;
    private final ModelMapper modelMapper;
    private final DriverFeignClient driverFeignClient;

    private DriverResponse getDriverById(long driverId) {
        return driverFeignClient.getDriver(driverId);
    }

    private RideResponse toDto(Ride ride) {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper.map(ride, RideResponse.class);
    }

    private Ride toEntity(CreateRideRequest request) {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper.map(request, Ride.class);
    }

    @Override
    public RideResponse add(CreateRideRequest request) {
        Ride ride = toEntity(request);
        setAdditionalFields(ride);
        rideRepository.save(ride);
        log.info("Created ride");
        RideResponse response = toDto(ride);
        response.setDriverResponse(getDriverById(ride.getDriverId()));
        driverFeignClient.changeStatus(response.getDriverResponse().getId());
        return response;
    }
    private void setAdditionalFields(Ride ride){
        ride.setDate(LocalDateTime.now());
        DriverResponse driver = driverFeignClient.getAvailable(1, 10, "id").getDrivers().get(0);//если список пуст то обработать
        ride.setDriverId(driver.getId());
        double price =generatePrice();
        ride.setPrice(price);
    }

    private double generatePrice() {
        Random random = new Random();
        return Math.round((3 + (100 - 3) * random.nextDouble()) * 100.0) / 100.0;
    }


    @Override
    @Transactional(readOnly = true)
    public RideResponse findById(Long id) {
        Ride ride = rideRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        log.info("Retrieving ride by id {}", id);
        RideResponse response = toDto(ride);
        response.setDriverResponse(getDriverById(ride.getDriverId()));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public RidesListResponse findAll(int page, int size, String sortingParam) {
        PageRequest pageRequest = getPageRequest(page, size, sortingParam);
        Page<Ride> ridePage = rideRepository.findAll(pageRequest);
        List<RideResponse> rides = ridePage.getContent()
                .stream().map(ride -> {
                    RideResponse response = toDto(ride);
                    response.setDriverResponse(getDriverById(ride.getDriverId()));
                    return response;
                }).toList();
        return RidesListResponse.builder().rides(rides).build();
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
        List<String> fieldNames = Arrays.stream(RideResponse.class.getDeclaredFields())
                .map(Field::getName)
                .toList();

        if (!fieldNames.contains(sortingParam)) {
            String errorMessage = String.format(INVALID_SORTING_MESSAGE, fieldNames);
            throw new InvalidRequestException(errorMessage);
        }
    }


    @Override
    public void update(UpdateRideRequest request, Long id) {
        if (rideRepository.findById(id).isEmpty()) {
            log.error("Ride with id {} was not found", id);
            throw new NotFoundException(id);
        }
        Ride ride = modelMapper.map(request,Ride.class);
        ride.setId(id);
        rideRepository.save(ride);
        log.info("Update ride with id {}", id);

    }

    @Override
    public void delete(Long id) {
        if (rideRepository.findById(id).isEmpty()) {
            log.error("Ride with id {} was not found", id);
            throw new NotFoundException(id);
        }
        rideRepository.deleteById(id);
        log.info("Delete ride with id {}", id);

    }

    @Override
    @Transactional(readOnly = true)
    public RidesListResponse getRidesByPassengerId(long passengerId, int page, int size, String orderBy) {
        PageRequest pageRequest = getPageRequest(page, size, orderBy);
        Page<Ride> ridesPage = rideRepository.findAllByPassengerId(passengerId, pageRequest);
        List<RideResponse> rides = ridesPage.getContent()
                .stream().map(ride -> {
                    RideResponse response = toDto(ride);
                    response.setDriverResponse(getDriverById(ride.getDriverId()));
                    return response;
                }).toList();
        log.info("Retrieving rides for passenger with id {}", passengerId);
        return RidesListResponse.builder()
                .rides(rides).build();
    }

    @Override
    @Transactional(readOnly = true)
    public RidesListResponse getRidesByDriverId(long driverId, int page, int size, String orderBy) {

        PageRequest pageRequest = getPageRequest(page, size, orderBy);
        Page<Ride> ridesPage = rideRepository.findAllByDriverId(driverId, pageRequest);
        List<RideResponse> rides = ridesPage.getContent()
                .stream().map(ride -> {
                    RideResponse response = toDto(ride);
                    response.setDriverResponse(getDriverById(ride.getDriverId()));
                    return response;
                }).toList();
        log.info("Retrieving rides for driver with id {}", driverId);
        return RidesListResponse.builder()
                .rides(rides).build();
    }

    @Override
    public void editStatus(long id, StatusRequest statusRequest) {
        if (rideRepository.findById(id).isEmpty()) {
            log.error("Ride with id {} was not found", id);
            throw new NotFoundException(id);
        }
        Ride ride = rideRepository.findById(id).get();
        if (Status.valueOf(statusRequest.getStatus()).equals(Status.FINISHED)) {
            driverFeignClient.changeStatus(ride.getDriverId());
        }
        ride.setStatus(Status.valueOf(statusRequest.getStatus()));
        rideRepository.save(ride);
    }
}
