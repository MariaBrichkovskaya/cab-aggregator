package com.modsen.passengerservice.component;

import com.modsen.passengerservice.client.DriverFeignClient;
import com.modsen.passengerservice.dto.request.PassengerRequest;
import com.modsen.passengerservice.dto.response.MessageResponse;
import com.modsen.passengerservice.dto.response.PassengerResponse;
import com.modsen.passengerservice.dto.response.PassengersListResponse;
import com.modsen.passengerservice.entity.Passenger;
import com.modsen.passengerservice.exception.AlreadyExistsException;
import com.modsen.passengerservice.exception.InvalidRequestException;
import com.modsen.passengerservice.exception.NotFoundException;
import com.modsen.passengerservice.mapper.PassengerMapper;
import com.modsen.passengerservice.repository.PassengerRepository;
import com.modsen.passengerservice.repository.RatingRepository;
import com.modsen.passengerservice.service.RatingService;
import com.modsen.passengerservice.service.impl.PassengerServiceImpl;
import com.modsen.passengerservice.service.impl.RatingServiceImpl;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Optional;

import static com.modsen.passengerservice.util.Messages.*;
import static com.modsen.passengerservice.util.PassengerTestUtils.*;
import static com.modsen.passengerservice.util.RatingTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@CucumberContextConfiguration
public class PassengerComponentTest {
    @Mock
    private PassengerRepository passengerRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private RatingRepository ratingRepository;

    private PassengerMapper passengerMapper;

    private PassengerServiceImpl passengerService;


    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        DriverFeignClient driverFeignClient = mock(DriverFeignClient.class);

        RatingService ratingService = new RatingServiceImpl(ratingRepository, passengerRepository, modelMapper, driverFeignClient);
        this.passengerMapper = new PassengerMapper(modelMapper, ratingService);
        this.passengerService = new PassengerServiceImpl(passengerRepository, passengerMapper);
    }

    private PassengerResponse passengerResponse;
    private Exception exception;
    private MessageResponse messageResponse;
    private PassengersListResponse passengersListResponse;


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
                .when(modelMapper)
                .map(any(Passenger.class), eq(PassengerResponse.class));
        doReturn(getDefaultRatings())
                .when(ratingRepository)
                .getRatingsByPassengerId(anyLong());

        Optional<Passenger> passenger = passengerRepository.findById(id);
        assertTrue(passenger.isPresent());
    }

    @Given("A passenger with id {long} doesn't exist")
    public void passengerWithIdNotExist(long id) {
        Optional<Passenger> passenger = passengerRepository.findById(id);
        assertFalse(passenger.isPresent());
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

        doReturn(Optional.of(getDefaultPassenger()))
                .when(passengerRepository)
                .findById(anyLong());
        doReturn(false)
                .when(passengerRepository)
                .existsByEmail(email);
        doReturn(false)
                .when(passengerRepository)
                .existsByPhone(phone);
        doReturn(passengerToSave)
                .when(modelMapper)
                .map(any(PassengerRequest.class), eq(Passenger.class));
        doReturn(savedPassenger)
                .when(passengerRepository)
                .save(passengerToSave);
        doReturn(expected)
                .when(modelMapper)
                .map(any(Passenger.class), eq(PassengerResponse.class));
        doReturn(getDefaultRatings())
                .when(ratingRepository)
                .getRatingsByPassengerId(anyLong());
    }

    @Given("A passenger with email {string} exists")
    public void passengerWithEmailExists(String email) {
        doReturn(true)
                .when(passengerRepository)
                .existsByEmail(email);
        doReturn(false)
                .when(passengerRepository)
                .existsByPhone(DEFAULT_PHONE);

        assertTrue(passengerRepository.existsByEmail(email));
    }

    @Given("A passenger with phone {string} exists")
    public void passengerWithPhoneExists(String phone) {
        doReturn(false)
                .when(passengerRepository)
                .existsByEmail(DEFAULT_EMAIL);
        doReturn(true)
                .when(passengerRepository)
                .existsByPhone(phone);

        assertTrue(passengerRepository.existsByPhone(phone));
    }

    @When("A create request with email {string}, phone {string} is passed to the add method")
    public void addPassengerMethodCalled(String email, String phone) {
        PassengerRequest createRequest = getPassengerRequest(email, phone);
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

        Passenger passengerToUpdate = getUpdatePassenger(email, phone);
        PassengerResponse notSavedPassenger = getUpdateResponse(email, phone);
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
                .when(modelMapper)
                .map(any(PassengerRequest.class), eq(Passenger.class));
        passengerToUpdate.setId(id);
        doReturn(passengerToUpdate)
                .when(passengerRepository)
                .save(any(Passenger.class));
        notSavedPassenger.setId(id);
        doReturn(notSavedPassenger)
                .when(modelMapper)
                .map(any(Passenger.class), eq(PassengerResponse.class));
        doReturn(getDefaultRatings())
                .when(ratingRepository)
                .getRatingsByPassengerId(anyLong());

    }

    @When("An update request with email {string}, phone {string} for passenger with id {long} is passed to the update method")
    public void updatePassengerMethodCalled(String email, String phone, long id) {
        var request = getPassengerRequest(email, phone);
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

    @Given("A list of passengers")
    public void givenAListOfPassengers() {
        Page<Passenger> passengerPage = new PageImpl<>(Arrays.asList(
                getDefaultPassenger(),
                getSecondPassenger()
        ));
        doReturn(Optional.of(getDefaultPassenger()))
                .when(passengerRepository)
                .findById(anyLong());
        doReturn(passengerPage)
                .when(passengerRepository)
                .findAll(any(PageRequest.class));
        doReturn(getDefaultPassengerResponse())
                .when(modelMapper)
                .map(any(Passenger.class), eq(PassengerResponse.class));
        doReturn(getDefaultRatings())
                .when(ratingRepository)
                .getRatingsByPassengerId(anyLong());
    }


    @When("The findAll method is called with valid parameters")
    public void whenTheFindAllMethodIsCalledWithValidParameters() {
        passengersListResponse = passengerService.findAll(VALID_PAGE, VALID_SIZE, VALID_ORDER_BY);
    }

    @Then("A list of passengers is returned")
    public void thenAListOfPassengersIsReturned() {
        assertNotNull(passengersListResponse);
        assertEquals(passengersListResponse.getPassengers().size(), 2);
    }

    @When("The findAll method is called with invalid page")
    public void whenTheFindAllMethodIsCalledWithInValidPage() {
        try {
            passengersListResponse = passengerService.findAll(INVALID_PAGE, VALID_SIZE, VALID_ORDER_BY);
        } catch (InvalidRequestException e) {
            exception = e;
        }
    }

    @Then("The InvalidRequestException should be thrown for invalid page")
    public void invalidRequestExceptionWithPageThrow() {
        assertThat(exception.getMessage()).isEqualTo(INVALID_PAGE_MESSAGE);
    }


}
