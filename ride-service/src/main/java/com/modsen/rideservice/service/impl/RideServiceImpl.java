package com.modsen.rideservice.service.impl;

import com.modsen.rideservice.client.DriverFeignClient;
import com.modsen.rideservice.client.PassengerFeignClient;
import com.modsen.rideservice.client.PaymentFeignClient;
import com.modsen.rideservice.exception.*;
import com.modsen.rideservice.kafka.RideProducer;
import com.modsen.rideservice.kafka.StatusProducer;
import com.modsen.rideservice.dto.request.*;
import com.modsen.rideservice.dto.response.*;
import com.modsen.rideservice.entity.Ride;
import com.modsen.rideservice.enums.PaymentMethod;
import com.modsen.rideservice.enums.RideStatus;
import com.modsen.rideservice.repository.RideRepository;
import com.modsen.rideservice.service.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
    private final PassengerFeignClient passengerFeignClient;
    private final PaymentFeignClient paymentFeignClient;
    private final RideProducer rideProducer;
    private final StatusProducer statusProducer;


    @Override
    public RideResponse add(CreateRideRequest request) {
        Ride ride = toEntity(request);
        PassengerResponse passengerResponse = validatePassenger(ride.getPassengerId());
        setAdditionalFields(ride);
        checkBalance(ride);
        Ride rideToSave = rideRepository.save(ride);
        rideProducer.sendMessage(RideRequest.builder()
                .id(rideToSave.getId())
                .build()
        );
        log.info("Created ride");
        return createRideResponse(rideToSave, passengerResponse);
    }


    @Override
    public void sendEditStatus(DriverForRideRequest request) {
        setDriver(request);
        EditDriverStatusRequest driverStatusRequest = EditDriverStatusRequest.builder()
                .driverId(request.getDriverId())
                .build();
        statusProducer.sendMessage(driverStatusRequest);
    }


    @Override
    @Transactional(readOnly = true)
    public RideResponse findById(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        log.info("Retrieving ride by id {}", id);

        return toDto(ride);
    }


    @Override
    @Transactional(readOnly = true)
    public RidesListResponse findAll(int page, int size, String sortingParam) {
        PageRequest pageRequest = getPageRequest(page, size, sortingParam);
        Page<Ride> ridePage = rideRepository.findAll(pageRequest);
        List<RideResponse> rides = ridePage.getContent()
                .stream().map(this::toDto)
                .toList();
        return RidesListResponse.builder()
                .rides(rides)
                .build();
    }


    @Override
    public RideResponse update(UpdateRideRequest request, Long id) {
        if (!rideRepository.existsById(id)) {
            log.error("Ride with id {} was not found", id);
            throw new NotFoundException(id);
        }

        Ride ride = modelMapper.map(request, Ride.class);
        ride.setId(id);
        Ride savedRide = rideRepository.save(ride);
        log.info("Update ride with id {}", id);

        return toDto(savedRide);
    }

    @Override
    public void delete(Long id) {
        if (!rideRepository.existsById(id)) {
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
        List<RideResponse> rides = ridesPage.getContent().stream()
                .map(this::toDto)
                .toList();
        log.info("Retrieving rides for passenger with id {}", passengerId);
        return RidesListResponse.builder()
                .rides(rides)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RidesListResponse getRidesByDriverId(long driverId, int page, int size, String orderBy) {
        PageRequest pageRequest = getPageRequest(page, size, orderBy);
        Page<Ride> ridesPage = rideRepository.findAllByDriverId(driverId, pageRequest);
        List<RideResponse> rides = ridesPage.getContent().stream()
                .map(this::toDto)
                .toList();
        log.info("Retrieving rides for driver with id {}", driverId);
        return RidesListResponse.builder()
                .rides(rides)
                .build();
    }

    @Override
    public void editStatus(long id, StatusRequest statusRequest) {
        Optional<Ride> optionalRide = rideRepository.findById(id);
        if (optionalRide.isEmpty()) {
            log.error("Ride with id {} was not found", id);
            throw new NotFoundException(id);
        }
        Ride ride = optionalRide.get();
        if (ride.getDriverId() == null) {
            throw new DriverIsEmptyException(EMPTY_DRIVER_MESSAGE);
        }
        if (RideStatus.FINISHED.equals(ride.getRideStatus())) {
            throw new AlreadyFinishedRideException("Ride already finished");
        }
        if (RideStatus.FINISHED.toString().equals(statusRequest.getStatus())) {
            EditDriverStatusRequest driverStatusRequest = EditDriverStatusRequest.builder()
                    .driverId(ride.getDriverId())
                    .build();
            statusProducer.sendMessage(driverStatusRequest);
        }

        ride.setRideStatus(RideStatus.valueOf(statusRequest.getStatus()));
        rideRepository.save(ride);
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

    private DriverResponse getDriverById(Long driverId) {
        if (driverId == null) {
            return null;
        }
        return driverFeignClient.getDriver(driverId);
    }

    private PassengerResponse getPassengerById(long passengerId) {
        return passengerFeignClient.getPassenger(passengerId);
    }

    private RideResponse toDto(Ride ride) {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        RideResponse response = modelMapper.map(ride, RideResponse.class);
        assignAndCheckDriver(response, ride.getDriverId());
        assignAndCheckPassenger(response, ride.getPassengerId());
        return response;
    }

    private Ride toEntity(CreateRideRequest request) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(request, Ride.class);
    }

    private RideResponse createRideResponse(Ride rideToSave, PassengerResponse passengerResponse) {
        RideResponse response = toDto(rideToSave);
        response.setDriverResponse(getDriverById(rideToSave.getDriverId()));
        response.setPassengerResponse(passengerResponse);
        return response;
    }

    private PassengerResponse validatePassenger(long passengerId) {
        try {
            return getPassengerById(passengerId);
        } catch (NotFoundException exception) {
            throw new InvalidRequestException("Passenger does not exist");
        }
    }

    private void checkBalance(Ride ride) {
        if (PaymentMethod.valueOf(ride.getPaymentMethod().name()).equals(PaymentMethod.CARD)) {
            try {
                charge(ride, (long) (ride.getPrice() * 100));
            } catch (BalanceException exception) {
                ride.setPaymentMethod(PaymentMethod.CASH);
                rideRepository.save(ride);
            }
        }
    }

    private void charge(Ride ride, long amount) {
        long passengerId = ride.getPassengerId();
        PassengerResponse passengerResponse = passengerFeignClient.getPassenger(passengerId);
        checkCustomer(passengerId, passengerResponse);
        CustomerChargeRequest request = CustomerChargeRequest.builder()
                .currency(CURRENCY).amount(amount)
                .passengerId(passengerResponse.getId())
                .build();
        paymentFeignClient.chargeFromCustomer(request);
    }

    private void checkCustomer(long passengerId, PassengerResponse passengerResponse) {
        try {
            paymentFeignClient.findCustomer(passengerId);
        } catch (NotFoundException e) {
            CustomerRequest customerRequest = CustomerRequest.builder()
                    .amount(1000000)
                    .phone(passengerResponse.getPhone())
                    .email(passengerResponse.getEmail())
                    .name(passengerResponse.getName())
                    .passengerId(passengerResponse.getId())
                    .build();
            paymentFeignClient.createCustomer(customerRequest);
        }
    }

    private void setAdditionalFields(Ride ride) {
        ride.setDate(LocalDateTime.now());
        double price = calculatePrice();
        ride.setPrice(price);
    }

    private double calculatePrice() {
        Random random = new Random();
        return Math.round((3 + (100 - 3) * random.nextDouble()) * 100.0) / 100.0;
    }

    private void setDriver(DriverForRideRequest request) {
        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new NotFoundException(request.getRideId()));
        ride.setDriverId(request.getDriverId());
        ride.setRideStatus(RideStatus.ACCEPTED);
        rideRepository.save(ride);
    }

    private void assignAndCheckPassenger(RideResponse response, long id) {
        try {
            response.setPassengerResponse(getPassengerById(id));
        } catch (NotFoundException exception) {
            response.setPassengerResponse(null);
        }
    }

    private void assignAndCheckDriver(RideResponse response, Long id) {
        try {
            if (id != null) {
                response.setDriverResponse(getDriverById(id));
            }
        } catch (NotFoundException exception) {
            response.setDriverResponse(null);
        }
    }
}
