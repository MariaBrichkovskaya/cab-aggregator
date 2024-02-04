package com.modsen.rideservice.service.impl;

import com.modsen.rideservice.dto.request.CreateRideRequest;
import com.modsen.rideservice.dto.request.CustomerChargeRequest;
import com.modsen.rideservice.dto.request.CustomerRequest;
import com.modsen.rideservice.dto.request.DriverForRideRequest;
import com.modsen.rideservice.dto.request.EditDriverStatusRequest;
import com.modsen.rideservice.dto.request.RideRequest;
import com.modsen.rideservice.dto.request.StatusRequest;
import com.modsen.rideservice.dto.request.UpdateRideRequest;
import com.modsen.rideservice.dto.response.DriverResponse;
import com.modsen.rideservice.dto.response.MessageResponse;
import com.modsen.rideservice.dto.response.PassengerResponse;
import com.modsen.rideservice.dto.response.RideResponse;
import com.modsen.rideservice.dto.response.RidesListResponse;
import com.modsen.rideservice.entity.Ride;
import com.modsen.rideservice.enums.PaymentMethod;
import com.modsen.rideservice.enums.RideStatus;
import com.modsen.rideservice.exception.AlreadyFinishedRideException;
import com.modsen.rideservice.exception.BalanceException;
import com.modsen.rideservice.exception.DriverIsEmptyException;
import com.modsen.rideservice.exception.InvalidRequestException;
import com.modsen.rideservice.exception.NotFoundException;
import com.modsen.rideservice.exception.PaymentFallbackException;
import com.modsen.rideservice.kafka.RideProducer;
import com.modsen.rideservice.kafka.StatusProducer;
import com.modsen.rideservice.repository.RideRepository;
import com.modsen.rideservice.service.DriverService;
import com.modsen.rideservice.service.PassengerService;
import com.modsen.rideservice.service.PaymentService;
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
    private final DriverService driverService;
    private final PassengerService passengerService;
    private final PaymentService paymentService;
    private final RideProducer rideProducer;
    private final StatusProducer statusProducer;


    @Override
    public RideResponse add(CreateRideRequest request) {
        Ride ride = toEntity(request);
        setAdditionalFields(ride);
        checkBalance(ride);
        Ride rideToSave = rideRepository.save(ride);
        rideProducer.sendMessage(RideRequest.builder()
                .id(rideToSave.getId())
                .build()
        );
        log.info("Created ride");
        return createRideResponse(rideToSave);
    }


    @Override
    public void sendEditStatus(DriverForRideRequest request) {
        setDriver(request);
        EditDriverStatusRequest driverStatusRequest = EditDriverStatusRequest.builder()
                .driverId(request.driverId())
                .build();
        statusProducer.sendMessage(driverStatusRequest);
    }


    @Override
    @Transactional(readOnly = true)
    public RideResponse findById(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> {
                            log.error("Ride with id {} is not found", id);
                            return new NotFoundException(id);
                        }
                );
        log.info("Retrieving ride by id {}", id);

        return fromEntityToRideResponse(ride);
    }


    @Override
    @Transactional(readOnly = true)
    public RidesListResponse findAll(int page, int size, String sortingParam) {
        PageRequest pageRequest = getPageRequest(page, size, sortingParam);
        Page<Ride> ridePage = rideRepository.findAll(pageRequest);
        List<RideResponse> rides = ridePage.getContent().stream()
                .map(this::fromEntityToRideResponse)
                .toList();
        return RidesListResponse.builder()
                .rides(rides)
                .build();
    }


    @Override
    public RideResponse update(UpdateRideRequest request, Long id) {
        Ride rideToUpdate = rideRepository.findById(id)
                .orElseThrow(() -> {
                            log.error("Ride with id {} is not found", id);
                            return new NotFoundException(id);
                        }
                );
        Ride ride = modelMapper.map(request, Ride.class);
        ride.setId(id);
        setFieldsForUpdate(ride, rideToUpdate);
        Ride savedRide = rideRepository.save(ride);
        log.info("Update ride with id {}", id);

        return fromEntityToRideResponse(savedRide);
    }

    private void setFieldsForUpdate(Ride ride, Ride rideToUpdate) {
        ride.setDate(rideToUpdate.getDate());
        ride.setRideStatus(rideToUpdate.getRideStatus());
        ride.setPaymentMethod(rideToUpdate.getPaymentMethod());
        ride.setDriverId(rideToUpdate.getDriverId());
        ride.setPassengerId(rideToUpdate.getPassengerId());
    }

    @Override
    public MessageResponse delete(Long id) {
        if (!rideRepository.existsById(id)) {
            log.error("Ride with id {} was not found", id);
            throw new NotFoundException(id);
        }
        rideRepository.deleteById(id);
        log.info("Delete ride with id {}", id);
        return MessageResponse.builder()
                .message(String.format(DELETE_MESSAGE, id))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RidesListResponse getRidesByPassengerId(long passengerId, int page, int size, String orderBy) {
        PageRequest pageRequest = getPageRequest(page, size, orderBy);
        Page<Ride> ridesPage = rideRepository.findAllByPassengerId(passengerId, pageRequest);
        List<RideResponse> rides = ridesPage.getContent().stream()
                .map(this::fromEntityToRideResponse)
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
                .map(this::fromEntityToRideResponse)
                .toList();
        log.info("Retrieving rides for driver with id {}", driverId);
        return RidesListResponse.builder()
                .rides(rides)
                .build();
    }

    @Override
    public RideResponse editStatus(long id, StatusRequest statusRequest) {
        Ride ride = rideRepository.findById(id).orElseThrow(() -> {
                    log.error("Ride with id {} is not found", id);
                    return new NotFoundException(id);
                }
        );
        checkStatus(ride, statusRequest);
        ride.setRideStatus(RideStatus.valueOf(statusRequest.getStatus()));
        Ride savedRide = rideRepository.save(ride);
        log.info("Status for ride with id {} was changed to {}", id, statusRequest.getStatus());

        return fromEntityToRideResponse(savedRide);
    }

    private void checkStatus(Ride ride, StatusRequest statusRequest) {
        if (ride.getDriverId() == null) {
            log.error(EMPTY_DRIVER_MESSAGE);
            throw new DriverIsEmptyException(EMPTY_DRIVER_MESSAGE);
        }
        if (RideStatus.FINISHED.equals(ride.getRideStatus())) {
            log.error(ALREADY_FINISHED_MESSAGE);
            throw new AlreadyFinishedRideException(ALREADY_FINISHED_MESSAGE);
        }
        if (RideStatus.FINISHED.toString().equals(statusRequest.getStatus())) {
            EditDriverStatusRequest driverStatusRequest = EditDriverStatusRequest.builder()
                    .driverId(ride.getDriverId())
                    .build();
            statusProducer.sendMessage(driverStatusRequest);
        }
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
            log.error(errorMessage);
            throw new InvalidRequestException(errorMessage);
        }
    }

    private DriverResponse getDriverById(Long driverId) {
        if (driverId == null) {
            return null;
        }
        return driverService.getDriver(driverId);
    }

    private PassengerResponse getPassengerById(long passengerId) {
        return passengerService.getPassenger(passengerId);
    }

    private RideResponse fromEntityToRideResponse(Ride ride) {
        RideResponse response = modelMapper.map(ride, RideResponse.class);
        response.setDriverResponse(getDriverById(ride.getDriverId()));
        response.setPassengerResponse(getPassengerById(ride.getPassengerId()));
        return response;
    }

    private Ride toEntity(CreateRideRequest request) {
        return modelMapper.map(request, Ride.class);
    }

    private RideResponse createRideResponse(Ride rideToSave) {
        return fromEntityToRideResponse(rideToSave);
    }


    private void checkBalance(Ride ride) {
        if (PaymentMethod.valueOf(ride.getPaymentMethod().name()).equals(PaymentMethod.CARD)) {
            try {
                charge(ride, (long) (ride.getPrice() * 100));
            } catch (BalanceException | PaymentFallbackException exception) {
                ride.setPaymentMethod(PaymentMethod.CASH);
                log.info("Payment method for ride with id {} was changed to cash", ride.getId());
                rideRepository.save(ride);
            }
        }
    }

    private void charge(Ride ride, long amount) {
        long passengerId = ride.getPassengerId();
        try {
            PassengerResponse passengerResponse = passengerService.getPassenger(passengerId);
            checkCustomer(passengerId, passengerResponse);
            CustomerChargeRequest request = CustomerChargeRequest.builder()
                    .currency(CURRENCY).amount(amount)
                    .passengerId(passengerResponse.getId())
                    .build();
            paymentService.chargeFromCustomer(request);
        } catch (PaymentFallbackException e) {
            ride.setPaymentMethod(PaymentMethod.CASH);
            log.info("Payment method for ride with id {} was changed to cash", ride.getId());
            rideRepository.save(ride);
        }


    }

    private void checkCustomer(long passengerId, PassengerResponse passengerResponse) {
        if (!paymentService.customerExistence(passengerId).isExist()) {
            CustomerRequest customerRequest = CustomerRequest.builder()
                    .amount(1000000)
                    .phone(passengerResponse.getPhone())
                    .email(passengerResponse.getEmail())
                    .name(passengerResponse.getName())
                    .passengerId(passengerResponse.getId())
                    .build();
            paymentService.createCustomer(customerRequest);
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
        Ride ride = rideRepository.findById(request.rideId())
                .orElseThrow(() -> {
                            log.error("Ride with id {} is not found", request.rideId());
                            return new NotFoundException(request.rideId());
                        }
                );
        ride.setDriverId(request.driverId());
        ride.setRideStatus(RideStatus.ACCEPTED);
        rideRepository.save(ride);
    }

}
