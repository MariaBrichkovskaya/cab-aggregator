package com.modsen.rideservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.rideservice.dto.request.CreateRideRequest;
import com.modsen.rideservice.dto.request.DriverForRideRequest;
import com.modsen.rideservice.dto.request.UpdateRideRequest;
import com.modsen.rideservice.dto.response.DriverResponse;
import com.modsen.rideservice.dto.response.PassengerResponse;
import com.modsen.rideservice.dto.response.RideResponse;
import com.modsen.rideservice.dto.response.ValidationExceptionResponse;
import com.modsen.rideservice.entity.Ride;
import com.modsen.rideservice.enums.PaymentMethod;
import com.modsen.rideservice.enums.RideStatus;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static com.modsen.rideservice.util.Messages.*;

@UtilityClass
public class TestUtils {
    public final long DEFAULT_ID = 1L;
    public final long NEW_ID = 2L;
    public final long NOT_FOUND_ID = 1000L;
    public final String ID_PARAM_NAME = "id";
    public final String DEFAULT_ID_PATH = "api/v1/rides/{id}";
    public final String STATUS_PATH = "api/v1/rides/{id}/status";
    public final String PAGE_PARAM_NAME = "page";
    public final String SIZE_PARAM_NAME = "size";
    public final String ORDER_BY_PARAM_NAME = "order_by";
    public final String DEFAULT_PATH = "/api/v1/rides";
    public final String GET_BY_PASSENGER_ID_PATH = "/api/v1/rides/passenger/{passenger_id}";
    public final String PASSENGER_PARAM_NAME = "passenger_id";
    public final RideStatus DEFAULT_STATUS = RideStatus.CREATED;
    public final double DEFAULT_PRICE = 5.0;
    public final String DEFAULT_DESTINATION_ADDRESS = "home";
    public final String DEFAULT_PICKUP_ADDRESS = "university";
    public final String DRIVER_PATH = "/api/v1/drivers/" + DEFAULT_ID;
    public final String PASSENGER_PATH = "/api/v1/passengers/" + DEFAULT_ID;
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
    private static final ResourceBundle validationMessages = ResourceBundle.getBundle("CustomValidationMessages");

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

    public RideResponse getUpdatedRideResponse() {
        return RideResponse.builder()
                .id(DEFAULT_ID)
                .rideStatus(RideStatus.ACCEPTED)
                .date(LocalDateTime.of(2024, 1, 13, 20, 32, 24, 470081000))
                .price(DEFAULT_PRICE)
                .destinationAddress(DEFAULT_DESTINATION_ADDRESS)
                .pickUpAddress(DEFAULT_PICKUP_ADDRESS)
                .paymentMethod(PaymentMethod.CASH.name())
                .build();
    }

    public ValidationExceptionResponse getRideValidationExceptionResponse() {
        String address = validationMessages.getString("address.not.empty.message");
        String price = validationMessages.getString("min.value.message");
        String passengerId = validationMessages.getString("min.value.message");
        return ValidationExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(VALIDATION_FAILED_MESSAGE)
                .errors(Map.of(
                        "passengerId", passengerId,
                        "destinationAddress", address,
                        "pickUpAddress", address,
                        "price", price


                ))
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

    public static <T> String fromObjectToString(T object) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getInvalidSortingMessage() {
        List<String> fieldNames = Arrays.stream(RideResponse.class.getDeclaredFields())
                .map(Field::getName)
                .toList();

        return String.format(INVALID_SORTING_MESSAGE, fieldNames);
    }
}
