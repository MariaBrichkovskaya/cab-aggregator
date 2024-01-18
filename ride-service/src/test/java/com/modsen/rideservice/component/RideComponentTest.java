package com.modsen.rideservice.component;

import com.modsen.rideservice.client.DriverFeignClient;
import com.modsen.rideservice.client.PassengerFeignClient;
import com.modsen.rideservice.client.PaymentFeignClient;
import com.modsen.rideservice.dto.request.CreateRideRequest;
import com.modsen.rideservice.dto.request.StatusRequest;
import com.modsen.rideservice.dto.request.UpdateRideRequest;
import com.modsen.rideservice.dto.response.MessageResponse;
import com.modsen.rideservice.dto.response.RideResponse;
import com.modsen.rideservice.dto.response.RidesListResponse;
import com.modsen.rideservice.entity.Ride;
import com.modsen.rideservice.enums.PaymentMethod;
import com.modsen.rideservice.enums.RideStatus;
import com.modsen.rideservice.exception.AlreadyFinishedRideException;
import com.modsen.rideservice.exception.InvalidRequestException;
import com.modsen.rideservice.exception.NotFoundException;
import com.modsen.rideservice.kafka.RideProducer;
import com.modsen.rideservice.kafka.StatusProducer;
import com.modsen.rideservice.repository.RideRepository;
import com.modsen.rideservice.service.impl.RideServiceImpl;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Optional;

import static com.modsen.rideservice.util.Messages.*;
import static com.modsen.rideservice.util.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@CucumberContextConfiguration
public class RideComponentTest {
    @Mock
    private RideRepository rideRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private DriverFeignClient driverFeignClient;
    @Mock
    private PassengerFeignClient passengerFeignClient;
    @Mock
    private PaymentFeignClient paymentFeignClient;
    @Mock
    private RideProducer rideProducer;
    @Mock
    private StatusProducer statusProducer;
    @InjectMocks
    private RideServiceImpl rideService;


    private RideResponse rideResponse;
    private Exception exception;
    private MessageResponse messageResponse;
    private RidesListResponse ridesListResponse;


    @Given("A ride with id {long} exists")
    public void rideWithIdExists(long id) {
        RideResponse expected = getDefaultRideResponse();
        Ride retrievedRide = getDefaultRide();
        doReturn(Optional.of(retrievedRide))
                .when(rideRepository)
                .findById(id);
        doReturn(true)
                .when(rideRepository)
                .existsById(id);
        doReturn(expected)
                .when(modelMapper)
                .map(any(Ride.class), eq(RideResponse.class));

        Optional<Ride> ride = rideRepository.findById(id);
        assertTrue(ride.isPresent());
    }

    @Given("A ride with id {long} for update exists")
    public void rideWithIdForUpdateExists(long id) {
        Ride rideToSave = getDefaultRideToSave();
        Ride savedRide = getDefaultRide();
        RideResponse response = getDefaultRideResponse();
        doReturn(Optional.of(getDefaultRide()))
                .when(rideRepository)
                .findById(id);
        doReturn(rideToSave)
                .when(modelMapper)
                .map(any(UpdateRideRequest.class), eq(Ride.class));
        doReturn(savedRide)
                .when(rideRepository)
                .save(rideToSave);
        doReturn(response)
                .when(modelMapper)
                .map(any(Ride.class), eq(RideResponse.class));

        Optional<Ride> ride = rideRepository.findById(id);
        assertTrue(ride.isPresent());
    }


    @Given("A ride with id {long} doesn't exist")
    public void rideWithIdNotExist(long id) {
        Optional<Ride> ride = rideRepository.findById(id);
        assertFalse(ride.isPresent());
    }

    @When("The id {long} is passed to the findById method")
    public void idPassedToFindByIdMethod(long id) {
        try {
            rideResponse = rideService.findById(id);
        } catch (NotFoundException e) {
            exception = e;
        }
    }

    @Then("The response should contain ride with id {long}")
    public void responseContainsRideDetails(long id) {
        Ride driver = rideRepository.findById(id).get();
        RideResponse expected = modelMapper.map(driver, RideResponse.class);

        assertEquals(rideResponse, expected);
    }

    @Then("The NotFoundException with id {long} should be thrown")
    public void rideNotFoundExceptionThrown(long id) {
        String expected = String.format(NOT_FOUND_WITH_ID_MESSAGE, id);
        String actual = exception.getMessage();

        assertEquals(actual, expected);
    }

    @When("The id {long} is passed to the deleteById method")
    public void idPassedToDeleteByIdMethod(long id) {
        try {
            messageResponse = rideService.delete(id);
        } catch (NotFoundException e) {
            exception = e;
        }
    }

    @Then("The response should contain message with id {long}")
    public void responseContainsDeleteMessage(long id) {
        MessageResponse expected = MessageResponse.builder()
                .message(String.format(DELETE_MESSAGE, id))
                .build();

        assertEquals(messageResponse, expected);
    }

    @Given("Create ride with default data")
    public void driverWithUniqueData() {
        Ride createdRide = getDefaultRideToSave();
        createdRide.setPaymentMethod(PaymentMethod.CASH);
        RideResponse expected = getDefaultRideResponse();
        Ride savedRide = getDefaultRide();
        doReturn(createdRide).when(modelMapper).map(any(CreateRideRequest.class), eq(Ride.class));
        doReturn(savedRide).when(rideRepository).save(createdRide);
        doReturn(expected).when(modelMapper).map(any(Ride.class), eq(RideResponse.class));

    }


    @When("A create request is passed to the add method")
    public void addPassengerMethodCalled() {
        CreateRideRequest createRequest = getRideRequestWhitCash();
        try {
            rideResponse = rideService.add(createRequest);
        } catch (NotFoundException e) {
            exception = e;
        }
    }


    @Then("The response should contain created ride")
    public void responseContainsCreatedRide() {
        RideResponse expected = getDefaultRideResponse();
        assertEquals(rideResponse.getDestinationAddress(), expected.getDestinationAddress());
        assertEquals(rideResponse.getPickUpAddress(), expected.getPickUpAddress());
        assertEquals(rideResponse.getPrice(), expected.getPrice());
        assertEquals(rideResponse.getPaymentMethod(), expected.getPaymentMethod());
    }


    @When("An update request for ride with id {long} is passed to the update method")
    public void updateRideMethodCalled(long id) {
        var request = getDefaultUpdateRideRequest();
        try {
            rideResponse = rideService.update(request, id);
        } catch (NotFoundException e) {
            exception = e;
        }
    }

    @Then("The response should contain updated ride with id {long}")
    public void responseContainsUpdatedRide(long id) {
        RideResponse actual = modelMapper.map(rideRepository.findById(id).get(), RideResponse.class);

        assertEquals(actual, rideResponse);
    }

    @Given("A ride with id {long} for editing status exists")
    public void rideWithIdForEditStatusExists(long id) {
        Ride ride = getDefaultRide();
        RideResponse response = getFinishedRideResponse();
        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findById(id);
        ride.setRideStatus(RideStatus.ACCEPTED);
        doReturn(ride).when(rideRepository).save(any(Ride.class));
        doReturn(response)
                .when(modelMapper)
                .map(any(Ride.class), eq(RideResponse.class));
    }


    @Given("A ride with id {long} for editing status exists when status finished")
    public void rideWithIdForEditStatusExistsWhenFinished(long id) {
        Ride finishedRide = getAlreadyFinishedRide();
        doReturn(Optional.of(finishedRide))
                .when(rideRepository)
                .findById(id);
    }

    @When("Ride id {long} is passed to the changeStatus method")
    public void idPassedToChangeStatusMethod(long id) {
        StatusRequest statusRequest = StatusRequest.builder()
                .status("FINISHED")
                .build();
        try {
            rideResponse = rideService.editStatus(id, statusRequest);
        } catch (NotFoundException | AlreadyFinishedRideException e) {
            exception = e;
        }
    }

    @Then("The AlreadyFinishedRideException should be thrown")
    public void AlreadyFinishedRideExceptionThrow() {
        assertEquals(exception.getMessage(), ALREADY_FINISHED_MESSAGE);
    }

    @Given("A list of rides")
    public void givenAListOfRides() {
        Page<Ride> ridePage = new PageImpl<>(Arrays.asList(getDefaultRide(), getAlreadyFinishedRide()));
        doReturn(Optional.of(getDefaultRide()))
                .when(rideRepository)
                .findById(anyLong());
        doReturn(ridePage)
                .when(rideRepository)
                .findAll(any(PageRequest.class));
        doReturn(getDefaultRideResponse())
                .when(modelMapper)
                .map(any(Ride.class), eq(RideResponse.class));
    }


    @When("The findAll method is called with valid parameters")
    public void whenTheFindAllMethodIsCalledWithValidParameters() {
        ridesListResponse = rideService.findAll(VALID_PAGE, VALID_SIZE, VALID_ORDER_BY);
    }

    @Then("A list of rides is returned")
    public void thenAListOfAvailableDriversIsReturned() {
        assertEquals(ridesListResponse.getRides().size(), 2);
    }

    @When("The findAll method is called with invalid page")
    public void whenTheFindAllMethodIsCalledWithInValidPage() {
        try {
            ridesListResponse = rideService.findAll(INVALID_PAGE, VALID_SIZE, VALID_ORDER_BY);
        } catch (InvalidRequestException e) {
            exception = e;
        }
    }

    @Then("The InvalidRequestException should be thrown for invalid page")
    public void invalidRequestExceptionWithPageThrow() {
        assertEquals(exception.getMessage(), INVALID_PAGE_MESSAGE);
    }

    @Given("History for passenger with id {long}")
    public void givenAListOfPassengerHistory(long id) {
        Page<Ride> ridePage = new PageImpl<>(Arrays.asList(getDefaultRide(), getAlreadyFinishedRide()));
        doReturn(ridePage)
                .when(rideRepository)
                .findAllByPassengerId(eq(id), any(PageRequest.class));
        doReturn(getDefaultRideResponse())
                .when(modelMapper)
                .map(any(Ride.class), eq(RideResponse.class));
    }

    @When("The getPassengerRides method is called for passenger with id {long}")
    public void whenTheHistoryMethodIsCalledWithInValidPage(long id) {
        try {
            ridesListResponse = rideService.getRidesByPassengerId(id, VALID_PAGE, VALID_SIZE, VALID_ORDER_BY);
        } catch (InvalidRequestException e) {
            exception = e;
        }
    }

    @Then("Passenger's history is returned")
    public void thenAPassengerHistoryIsReturned() {
        assertEquals(ridesListResponse.getRides().size(), 2);
    }

    @Given("History for driver with id {long}")
    public void givenAListOfDriverHistory(long id) {
        Page<Ride> ridePage = new PageImpl<>(Arrays.asList(getDefaultRide(), getAlreadyFinishedRide()));
        doReturn(ridePage)
                .when(rideRepository)
                .findAllByDriverId(eq(id), any(PageRequest.class));
        doReturn(getDefaultRideResponse())
                .when(modelMapper)
                .map(any(Ride.class), eq(RideResponse.class));
    }

    @When("The getDriverRides method is called for driver with id {long}")
    public void whenTheDriverHistoryMethodIsCalledWithInValidPage(long id) {
        try {
            ridesListResponse = rideService.getRidesByDriverId(id, VALID_PAGE, VALID_SIZE, VALID_ORDER_BY);
        } catch (InvalidRequestException e) {
            exception = e;
        }
    }

    @Then("Driver's history is returned")
    public void thenAHistoryIsReturned() {
        assertEquals(ridesListResponse.getRides().size(), 2);
    }
}
