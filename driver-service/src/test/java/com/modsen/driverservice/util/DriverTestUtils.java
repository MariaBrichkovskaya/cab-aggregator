package com.modsen.driverservice.util;

import com.modsen.driverservice.dto.request.DriverRequest;
import com.modsen.driverservice.dto.request.RideRequest;
import com.modsen.driverservice.dto.response.DriverResponse;
import com.modsen.driverservice.dto.response.PassengerResponse;
import com.modsen.driverservice.dto.response.ValidationExceptionResponse;
import com.modsen.driverservice.entity.Driver;
import com.modsen.driverservice.enums.Status;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static com.modsen.driverservice.util.Messages.*;

@UtilityClass
public class DriverTestUtils {
    public final long DEFAULT_ID = 1L;
    public final long NEW_ID = 4L;
    public final long NOT_FOUND_ID = 100L;
    public final String DEFAULT_NAME = "Name";
    public final String DEFAULT_SURNAME = "Surname";
    public final String DEFAULT_PHONE = "80291234567";
    public final String DEFAULT_EMAIL = "123@gmail.com";
    public final double DEFAULT_RATING = 5.0;
    public final double AVERAGE_RATING = 4.0;
    public final String UNIQUE_PHONE = "80291237567";
    public final Integer DEFAULT_SCORE = 4;
    public final int INVALID_PAGE = -1;
    public final int VALID_PAGE = 1;
    public final int INVALID_SIZE = -1;
    public final int VALID_SIZE = 10;
    public final String INVALID_ORDER_BY = "qwerty";
    public final String VALID_ORDER_BY = "id";
    public final String ID_PARAM_NAME = "id";
    public final String DEFAULT_ID_PATH = "/api/v1/drivers/{id}";
    public final String PAGE_PARAM_NAME = "page";
    public final String SIZE_PARAM_NAME = "size";
    public final String ORDER_BY_PARAM_NAME = "order_by";
    public final String DEFAULT_PATH = "/api/v1/drivers";
    public final String INVALID_NAME = null;
    public final String INVALID_SURNAME = null;
    public final String INVALID_PHONE = null;
    private static final ResourceBundle validationMessages = ResourceBundle.getBundle("CustomValidationMessages");

    public ValidationExceptionResponse getDriverValidationExceptionResponse() {
        String nameMessage = validationMessages.getString("name.not.empty.message");
        String surnameMessage = validationMessages.getString("surname.not.empty.message");
        String phoneMessage = validationMessages.getString("phone.not.empty.message");
        return ValidationExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(VALIDATION_FAILED_MESSAGE)
                .errors(Map.of(
                        "name", nameMessage,
                        "surname", surnameMessage,
                        "phone", phoneMessage
                ))
                .build();
    }

    public Driver getDefaultDriver() {
        return Driver.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(DEFAULT_PHONE)
                .status(Status.AVAILABLE)
                .build();
    }

    public Driver getSecondDriver() {
        return Driver.builder()
                .id(NEW_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(UNIQUE_PHONE)
                .status(Status.AVAILABLE)
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


    public Driver getNotSavedDriver() {
        return Driver.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(DEFAULT_PHONE)
                .build();
    }

    public DriverRequest getDriverRequest() {
        return DriverRequest.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(DEFAULT_PHONE)
                .build();
    }

    public DriverRequest getUniqueRequest() {
        return DriverRequest.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(UNIQUE_PHONE)
                .build();
    }

    public Driver getUpdateDriver() {
        return Driver.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(UNIQUE_PHONE)
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


    public RideRequest getDefaultRideRequest() {
        return RideRequest.builder()
                .id(DEFAULT_ID)
                .build();
    }

    public String getInvalidSortingMessage() {
        List<String> fieldNames = Arrays.stream(DriverResponse.class.getDeclaredFields())
                .map(Field::getName)
                .toList();

        return String.format(INVALID_SORTING_MESSAGE, fieldNames);
    }

}
