package com.modsen.rideservice.util;

import com.modsen.rideservice.dto.request.CreateRideRequest;
import com.modsen.rideservice.dto.request.DriverForRideRequest;
import com.modsen.rideservice.dto.request.UpdateRideRequest;
import com.modsen.rideservice.dto.response.DriverResponse;
import com.modsen.rideservice.dto.response.PassengerResponse;
import com.modsen.rideservice.dto.response.RideResponse;
import com.modsen.rideservice.entity.Ride;
import com.modsen.rideservice.enums.PaymentMethod;
import com.modsen.rideservice.enums.RideStatus;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class TestUtils {
    public final long DEFAULT_ID = 1L;
    public final long NEW_ID = 2L;
    public final RideStatus DEFAULT_STATUS = RideStatus.CREATED;
    public final double DEFAULT_PRICE = 5.0;
    public final String DEFAULT_DESTINATION_ADDRESS = "home";
    public final String DEFAULT_PICKUP_ADDRESS = "university";
    public final PaymentMethod DEFAULT_PAYMENT_METHOD = PaymentMethod.CARD;
    public final String DEFAULT_NAME = "Name";
    public final String DEFAULT_SURNAME = "Surname";
    public final String DEFAULT_PHONE = "80291234567";
    public final String DEFAULT_EMAIL = "123@gmail.com";
    public final double DEFAULT_RATING = 5.0;
    public final int INVALID_PAGE = -1;
    public final int VALID_PAGE = 1;
    public final int INVALID_SIZE = -1;
    public final int VALID_SIZE = 10;
    public final String INVALID_ORDER_BY = "qwerty";
    public final String VALID_ORDER_BY = "id";

    public Ride getDefaultRide() {
        return Ride.builder()
                .id(DEFAULT_ID)
                .rideStatus(DEFAULT_STATUS)
                .date(LocalDateTime.now())
                .price(DEFAULT_PRICE)
                .destinationAddress(DEFAULT_DESTINATION_ADDRESS)
                .pickUpAddress(DEFAULT_PICKUP_ADDRESS)
                .passengerId(DEFAULT_ID)
                .paymentMethod(DEFAULT_PAYMENT_METHOD)
                .driverId(DEFAULT_ID)
                .build();
    }

    public Ride getDefaultRideWhereDriverNotAssign() {
        return Ride.builder()
                .id(DEFAULT_ID)
                .rideStatus(DEFAULT_STATUS)
                .date(LocalDateTime.now())
                .price(DEFAULT_PRICE)
                .destinationAddress(DEFAULT_DESTINATION_ADDRESS)
                .pickUpAddress(DEFAULT_PICKUP_ADDRESS)
                .passengerId(DEFAULT_ID)
                .paymentMethod(DEFAULT_PAYMENT_METHOD)
                .driverId(null)
                .build();
    }

    public Ride getAlreadyFinishedRide() {
        return Ride.builder()
                .id(NEW_ID)
                .rideStatus(RideStatus.FINISHED)
                .date(LocalDateTime.now())
                .price(DEFAULT_PRICE)
                .destinationAddress(DEFAULT_DESTINATION_ADDRESS)
                .pickUpAddress(DEFAULT_PICKUP_ADDRESS)
                .passengerId(DEFAULT_ID)
                .paymentMethod(DEFAULT_PAYMENT_METHOD)
                .driverId(DEFAULT_ID)
                .build();
    }

    public Ride getDefaultRideToSave() {
        return Ride.builder()
                .destinationAddress(DEFAULT_DESTINATION_ADDRESS)
                .pickUpAddress(DEFAULT_PICKUP_ADDRESS)
                .passengerId(DEFAULT_ID)
                .paymentMethod(DEFAULT_PAYMENT_METHOD)
                .build();
    }

    public CreateRideRequest getRideRequestWhitCard() {
        return CreateRideRequest.builder()
                .destinationAddress(DEFAULT_DESTINATION_ADDRESS)
                .pickUpAddress(DEFAULT_PICKUP_ADDRESS)
                .passengerId(DEFAULT_ID)
                .paymentMethod(DEFAULT_PAYMENT_METHOD.name())
                .build();
    }

    public CreateRideRequest getRideRequestWhitCash() {
        return CreateRideRequest.builder()
                .destinationAddress(DEFAULT_DESTINATION_ADDRESS)
                .pickUpAddress(DEFAULT_PICKUP_ADDRESS)
                .passengerId(DEFAULT_ID)
                .paymentMethod(PaymentMethod.CASH.name())
                .build();
    }

    public UpdateRideRequest getDefaultUpdateRideRequest() {
        return UpdateRideRequest.builder()
                .destinationAddress(DEFAULT_DESTINATION_ADDRESS)
                .pickUpAddress(DEFAULT_PICKUP_ADDRESS)
                .passengerId(DEFAULT_ID)
                .price(DEFAULT_PRICE)
                .build();
    }

    public DriverResponse getDefaultDriverResponse() {
        return DriverResponse.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING)
                .build();
    }

    public PassengerResponse getDefaultPassengerResponse() {
        return PassengerResponse.builder()
                .name(DEFAULT_NAME)
                .id(DEFAULT_ID)
                .name(DEFAULT_SURNAME)
                .phone(DEFAULT_PHONE)
                .email(DEFAULT_EMAIL)
                .build();
    }

    public RideResponse getDefaultRideResponse() {
        return RideResponse.builder()
                .id(DEFAULT_ID)
                .rideStatus(DEFAULT_STATUS)
                .date(LocalDateTime.now())
                .price(DEFAULT_PRICE)
                .destinationAddress(DEFAULT_DESTINATION_ADDRESS)
                .pickUpAddress(DEFAULT_PICKUP_ADDRESS)
                .paymentMethod(DEFAULT_PAYMENT_METHOD.name())
                .build();
    }

    public RideResponse getFinishedRideResponse() {
        return RideResponse.builder()
                .id(DEFAULT_ID)
                .rideStatus(RideStatus.FINISHED)
                .date(LocalDateTime.now())
                .price(DEFAULT_PRICE)
                .destinationAddress(DEFAULT_DESTINATION_ADDRESS)
                .pickUpAddress(DEFAULT_PICKUP_ADDRESS)
                .paymentMethod(DEFAULT_PAYMENT_METHOD.name())
                .build();
    }

    public DriverForRideRequest getDefaultDriverForRideRequest() {
        return DriverForRideRequest.builder()
                .rideId(DEFAULT_ID)
                .driverId(DEFAULT_ID)
                .build();
    }
}
