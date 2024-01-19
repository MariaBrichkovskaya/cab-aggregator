package com.modsen.passengerservice.util;

import com.modsen.passengerservice.dto.request.PassengerRequest;
import com.modsen.passengerservice.dto.response.PassengerResponse;
import com.modsen.passengerservice.dto.response.ValidationExceptionResponse;
import com.modsen.passengerservice.entity.Passenger;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static com.modsen.passengerservice.util.Messages.*;

@UtilityClass
public class PassengerTestUtils {
    public final long DEFAULT_ID = 1L;
    public final long NEW_ID = 4L;
    public final String DEFAULT_NAME = "Name";
    public final String DEFAULT_SURNAME = "Surname";
    public final String DEFAULT_EMAIL = "maria@example.com";
    public final String DEFAULT_PHONE = "80291234567";
    public final String INVALID_NAME = null;
    public final String INVALID_SURNAME = null;
    public final String INVALID_EMAIL = "maria";
    public final String INVALID_PHONE = null;
    public final double DEFAULT_RATING = 5.0;
    public final String UNIQUE_EMAIL = "123@example.com";
    public final String UNIQUE_PHONE = "80299999999";
    public final Integer DEFAULT_SCORE = 5;
    public final double DEFAULT_AVERAGE_RATING = 5.0;
    public final int INVALID_PAGE = -1;
    public final int VALID_PAGE = 1;
    public final int INVALID_SIZE = -1;
    public final int VALID_SIZE = 10;
    public final String INVALID_ORDER_BY = "qwerty";
    public final String VALID_ORDER_BY = "id";
    public final long NOT_FOUND_ID = 1000L;
    public final String ID_PARAM_NAME = "id";
    public final String DEFAULT_ID_PATH = "/api/v1/passengers/{id}";
    public final String PAGE_PARAM_NAME = "page";
    public final String SIZE_PARAM_NAME = "size";
    public final String ORDER_BY_PARAM_NAME = "order_by";
    public final String DEFAULT_PATH = "/api/v1/passengers";

    private static final ResourceBundle validationMessages = ResourceBundle.getBundle("CustomValidationMessages");

    public ValidationExceptionResponse getPassengerValidationExceptionResponse() {
        String nameMessage = validationMessages.getString("name.not.empty.message");
        String surnameMessage = validationMessages.getString("surname.not.empty.message");
        String emailMessage = validationMessages.getString("invalid.email.message");
        String phoneMessage = validationMessages.getString("phone.not.empty.message");
        return ValidationExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(VALIDATION_FAILED_MESSAGE)
                .errors(Map.of(
                        "name", nameMessage,
                        "surname", surnameMessage,
                        "email", emailMessage,
                        "phone", phoneMessage
                ))
                .build();
    }

    public PassengerRequest getPassengerRequest(String email, String phone) {
        return PassengerRequest.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(email)
                .phone(phone)
                .build();
    }

    public Passenger getUpdatePassenger(String email, String phone) {
        return Passenger.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(email)
                .phone(phone)
                .build();
    }

    public PassengerResponse getUpdateResponse(String email, String phone) {
        return PassengerResponse.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(email)
                .phone(phone)
                .build();
    }

    public Passenger getDefaultPassenger() {
        return Passenger.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .build();
    }

    public Passenger getSecondPassenger() {
        return Passenger.builder()
                .id(NEW_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(UNIQUE_EMAIL)
                .phone(UNIQUE_PHONE)
                .build();
    }

    public PassengerResponse getDefaultPassengerResponse() {
        return PassengerResponse.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING)
                .build();
    }

    public List<PassengerResponse> getDefaultPassengersListResponse() {
        PassengerResponse second = PassengerResponse.builder()
                .id(NEW_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(UNIQUE_EMAIL)
                .phone(UNIQUE_PHONE)
                .rating(DEFAULT_RATING)
                .build();
        return List.of(getDefaultPassengerResponse(), second);
    }


    public Passenger getNotSavedPassenger() {
        return Passenger.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .build();
    }

    public PassengerRequest getPassengerRequest() {
        return PassengerRequest.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .build();
    }

    public PassengerRequest getUniquePassengerRequest() {
        return PassengerRequest.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(UNIQUE_EMAIL)
                .phone(UNIQUE_PHONE)
                .build();
    }

    public Passenger getUpdatePassenger() {
        return Passenger.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(UNIQUE_EMAIL)
                .phone(UNIQUE_PHONE)
                .build();
    }


    public String getInvalidSortingMessage() {
        List<String> fieldNames = Arrays.stream(PassengerResponse.class.getDeclaredFields())
                .map(Field::getName)
                .toList();

        return String.format(INVALID_SORTING_MESSAGE, fieldNames);
    }

}
