package com.modsen.passengerservice.component;

import com.modsen.passengerservice.dto.request.PassengerRequest;
import com.modsen.passengerservice.dto.response.MessageResponse;
import com.modsen.passengerservice.dto.response.PassengerResponse;
import com.modsen.passengerservice.entity.Passenger;
import com.modsen.passengerservice.exception.AlreadyExistsException;
import com.modsen.passengerservice.exception.NotFoundException;
import com.modsen.passengerservice.mapper.PassengerMapper;
import com.modsen.passengerservice.repository.PassengerRepository;
import com.modsen.passengerservice.service.impl.PassengerServiceImpl;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.RequiredArgsConstructor;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static com.modsen.passengerservice.util.Messages.*;
import static com.modsen.passengerservice.util.PassengerTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@RequiredArgsConstructor
@CucumberContextConfiguration
@RunWith(MockitoJUnitRunner.class)
public class PassengerComponentTest {

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private PassengerMapper passengerMapper;

    @InjectMocks
    private PassengerServiceImpl passengerService;

    private PassengerResponse passengerResponse;
    private Exception exception;
    private MessageResponse messageResponse;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(PassengerComponentTest.this);
    }

    @Given("A passenger with id {long} exists")
    public void passengerWithIdExists(long id) {
        PassengerResponse expected = getDefaultPassengerResponse();
        Passenger retrievedPassenger = getDefaultPassenger();

        doReturn(Optional.of(retrievedPassenger))
                .when(passengerRepository)
                .findById(id);
        doReturn(true)
                .when(passengerRepository)
                .existsById(id);
        doReturn(expected)
                .when(passengerMapper)
                .toPassengerResponse(retrievedPassenger);

        Optional<Passenger> passenger = passengerRepository.findById(id);
        assertThat(passenger.isPresent()).isEqualTo(true);
    }

    @Given("A passenger with id {long} doesn't exist")
    public void passengerWithIdNotExist(long id) {
        Optional<Passenger> passenger = passengerRepository.findById(id);
        assertThat(passenger.isPresent()).isEqualTo(false);
    }

    @When("The id {long} is passed to the findById method")
    public void idPassedToFindByIdMethod(long id) {
        try {
            passengerResponse = passengerService.findById(id);
        } catch (NotFoundException e) {
            exception = e;
        }
    }

    @Then("The response should contain passenger with id {long}")
    public void responseContainsPassengerDetails(long id) {
        Passenger passenger = passengerRepository.findById(id).get();
        PassengerResponse expected = passengerMapper.toPassengerResponse(passenger);

        assertThat(passengerResponse).isEqualTo(expected);
    }

    @Then("The NotFoundException with id {long} should be thrown")
    public void passengerNotFoundExceptionThrown(long id) {
        String expected = String.format(NOT_FOUND_WITH_ID_MESSAGE, id);
        String actual = exception.getMessage();

        assertThat(actual).isEqualTo(expected);
    }

    @When("The id {long} is passed to the deleteById method")
    public void idPassedToDeleteByIdMethod(long id) {
        try {
            messageResponse = passengerService.delete(id);
        } catch (NotFoundException e) {
            exception = e;
        }
    }

    @Then("The response should contain message with id {long}")
    public void responseContainsDeleteMessage(long id) {
        MessageResponse expected = MessageResponse.builder()
                .message(String.format(DELETE_PASSENGER_MESSAGE, id))
                .build();

        assertThat(messageResponse).isEqualTo(expected);
    }

    @Given("A passenger with email {string} and phone {string} doesn't exist")
    public void passengerWithUniqueData(String email, String phone) {
        PassengerResponse expected = getDefaultPassengerResponse();
        Passenger passengerToSave = getNotSavedPassenger();
        Passenger savedPassenger = getDefaultPassenger();

        doReturn(false)
                .when(passengerRepository)
                .existsByEmail(email);
        doReturn(false)
                .when(passengerRepository)
                .existsByPhone(phone);
        doReturn(passengerToSave)
                .when(passengerMapper)
                .toEntity(any(PassengerRequest.class));
        doReturn(savedPassenger)
                .when(passengerRepository)
                .save(passengerToSave);
        doReturn(expected)
                .when(passengerMapper)
                .toPassengerResponse(any(Passenger.class));

    }

    @Given("A passenger with email {string} exists")
    public void passengerWithEmailExists(String email) {
        doReturn(true)
                .when(passengerRepository)
                .existsByEmail(email);
        doReturn(false)
                .when(passengerRepository)
                .existsByPhone(DEFAULT_PHONE);

        assertThat(passengerRepository.existsByEmail(email)).isEqualTo(true);
    }

    @Given("A passenger with phone {string} exists")
    public void passengerWithPhoneExists(String phone) {
        doReturn(false)
                .when(passengerRepository)
                .existsByEmail(DEFAULT_EMAIL);
        doReturn(true)
                .when(passengerRepository)
                .existsByPhone(phone);

        assertThat(passengerRepository.existsByPhone(phone)).isEqualTo(true);
    }

    @When("A create request with email {string}, phone {string} is passed to the add method")
    public void addPassengerMethodCalled(String email, String phone) {
        PassengerRequest createRequest = PassengerRequest.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(email)
                .phone(phone)
                .build();
        try {
            passengerResponse = passengerService.add(createRequest);
        } catch (AlreadyExistsException e) {
            exception = e;
        }
    }


    @Then("The response should contain created passenger")
    public void responseContainsCreatedPassenger() {
        var expected = getDefaultPassengerResponse();
        assertThat(passengerResponse).isEqualTo(expected);
    }

    @Then("The AlreadyExistsException should be thrown")
    public void notFoundExceptionThrown() {
        assertThat(exception.getMessage()).isEqualTo(PASSENGER_ALREADY_EXISTS_MESSAGE);
    }

    @Given("A passenger with id {long} exists when email {string} and phone {string} doesn't exist")
    public void UpdatePassengerWithUniqueData(long id, String email, String phone) {

        Passenger passengerToUpdate = Passenger.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(email)
                .phone(phone)
                .build();
        PassengerResponse notSavedPassenger = PassengerResponse.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(email)
                .phone(phone)
                .build();
        Passenger savedPassenger = Passenger.builder()
                .id(id)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(email)
                .phone(phone)
                .build();
        doReturn(Optional.of(passengerToUpdate))
                .when(passengerRepository)
                .findById(id);
        doReturn(false)
                .when(passengerRepository)
                .existsByPhone(phone);
        doReturn(false)
                .when(passengerRepository)
                .existsByEmail(email);
        doReturn(passengerToUpdate)
                .when(passengerMapper)
                .toEntity(any(PassengerRequest.class));
        doReturn(savedPassenger)
                .when(passengerRepository)
                .save(any(Passenger.class));
        notSavedPassenger.setId(id);
        doReturn(notSavedPassenger)
                .when(passengerMapper)
                .toPassengerResponse(any(Passenger.class));

    }

    @When("An update request with email {string}, phone {string} for passenger with id {long} is passed to the update method")
    public void updatePassengerMethodCalled(String email, String phone, long id) {
        var request = PassengerRequest.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(email)
                .phone(phone)
                .build();
        try {
            passengerResponse = passengerService.update(request, id);
        } catch (NotFoundException | AlreadyExistsException e) {
            exception = e;
        }
    }

    @Then("The response should contain updated passenger with id {long}")
    public void responseContainsUpdatedPassenger(long id) {
        PassengerResponse actual = passengerMapper.toPassengerResponse(passengerRepository.findById(id).get());
        assertThat(actual).isEqualTo(passengerResponse);
    }


}
