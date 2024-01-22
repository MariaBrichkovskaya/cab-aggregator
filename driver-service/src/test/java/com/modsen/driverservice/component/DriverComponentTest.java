package com.modsen.driverservice.component;

import com.modsen.driverservice.client.PassengerFeignClient;
import com.modsen.driverservice.dto.request.DriverRequest;
import com.modsen.driverservice.dto.response.DriverResponse;
import com.modsen.driverservice.dto.response.DriversListResponse;
import com.modsen.driverservice.dto.response.MessageResponse;
import com.modsen.driverservice.entity.Driver;
import com.modsen.driverservice.enums.Status;
import com.modsen.driverservice.exception.AlreadyExistsException;
import com.modsen.driverservice.exception.InvalidRequestException;
import com.modsen.driverservice.exception.NotFoundException;
import com.modsen.driverservice.kafka.DriverProducer;
import com.modsen.driverservice.mapper.DriverMapper;
import com.modsen.driverservice.repository.DriverRepository;
import com.modsen.driverservice.repository.RatingRepository;
import com.modsen.driverservice.service.DriverService;
import com.modsen.driverservice.service.RatingService;
import com.modsen.driverservice.service.impl.DriverServiceImpl;
import com.modsen.driverservice.service.impl.RatingServiceImpl;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Optional;

import static com.modsen.driverservice.util.DriverTestUtils.*;
import static com.modsen.driverservice.util.Messages.*;
import static com.modsen.driverservice.util.RatingTestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@CucumberContextConfiguration
public class DriverComponentTest {
    private DriverRepository driverRepository;

    private ModelMapper modelMapper;

    private RatingRepository ratingRepository;

    private DriverMapper driverMapper;

    private DriverService driverService;


    @Before
    public void setUp() {
        this.driverRepository = mock(DriverRepository.class);
        this.modelMapper = mock(ModelMapper.class);
        this.ratingRepository = mock(RatingRepository.class);
        PassengerFeignClient passengerFeignClient = mock(PassengerFeignClient.class);
        DriverProducer driverProducer = mock(DriverProducer.class);

        RatingService ratingService = new RatingServiceImpl(ratingRepository, driverRepository, modelMapper, passengerFeignClient);
        this.driverMapper = new DriverMapper(modelMapper, ratingService);
        this.driverService = new DriverServiceImpl(driverMapper, driverRepository, driverProducer);

    }

    private DriverResponse driverResponse;
    private Exception exception;
    private MessageResponse messageResponse;
    private DriversListResponse driversListResponse;


    @Given("A driver with id {long} exists")
    public void driverWithIdExists(long id) {
        DriverResponse expected = getDefaultDriverResponse();
        Driver retrievedDriver = getDefaultDriver();
        doReturn(Optional.of(retrievedDriver))
                .when(driverRepository)
                .findByIdAndActiveIsTrue(id);
        doReturn(Optional.of(retrievedDriver))
                .when(driverRepository)
                .findById(id);
        doReturn(true)
                .when(driverRepository)
                .existsById(id);
        doReturn(expected)
                .when(modelMapper)
                .map(any(Driver.class), eq(DriverResponse.class));
        doReturn(getDefaultRatings())
                .when(ratingRepository)
                .getRatingsByDriverId(anyLong());

        Optional<Driver> driver = driverRepository.findByIdAndActiveIsTrue(id);
        assertTrue(driver.isPresent());
    }

    @Given("A driver with id {long} doesn't exist")
    public void driverWithIdNotExist(long id) {
        Optional<Driver> driver = driverRepository.findByIdAndActiveIsTrue(id);
        assertFalse(driver.isPresent());
    }

    @When("The id {long} is passed to the findById method")
    public void idPassedToFindByIdMethod(long id) {
        try {
            driverResponse = driverService.findById(id);
        } catch (NotFoundException e) {
            exception = e;
        }
    }

    @Then("The response should contain driver with id {long}")
    public void responseContainsDriverDetails(long id) {
        Driver driver = driverRepository.findById(id).get();
        DriverResponse expected = driverMapper.toDriverResponse(driver);

        assertEquals(driverResponse, expected);
    }

    @Then("The NotFoundException with id {long} should be thrown")
    public void driverNotFoundExceptionThrown(long id) {
        String expected = String.format(NOT_FOUND_WITH_DRIVER_ID_MESSAGE, id);
        String actual = exception.getMessage();

        assertEquals(actual, expected);
    }

    @When("The id {long} is passed to the deleteById method")
    public void idPassedToDeleteByIdMethod(long id) {
        try {
            messageResponse = driverService.delete(id);
        } catch (NotFoundException e) {
            exception = e;
        }
    }

    @Then("The response should contain message with id {long}")
    public void responseContainsDeleteMessage(long id) {
        MessageResponse expected = MessageResponse.builder()
                .message(String.format(DELETE_DRIVER_MESSAGE, id))
                .build();

        assertEquals(messageResponse, expected);
    }

    @Given("A driver with phone {string} doesn't exist")
    public void driverWithUniqueData(String phone) {
        DriverResponse expected = getDefaultDriverResponse();
        Driver driverToSave = getNotSavedDriver();
        Driver savedDriver = getDefaultDriver();
        doReturn(Optional.of(getDefaultDriver()))
                .when(driverRepository)
                .findById(anyLong());
        doReturn(false)
                .when(driverRepository)
                .existsByPhone(phone);
        doReturn(driverToSave)
                .when(modelMapper)
                .map(any(DriverRequest.class), eq(Driver.class));
        doReturn(savedDriver)
                .when(driverRepository)
                .save(driverToSave);
        doReturn(expected)
                .when(modelMapper)
                .map(any(Driver.class), eq(DriverResponse.class));

        assertFalse(driverRepository.existsByPhone(phone));
    }


    @Given("A driver with phone {string} exists")
    public void driverWithPhoneExists(String phone) {
        doReturn(true)
                .when(driverRepository)
                .existsByPhone(phone);

        assertTrue(driverRepository.existsByPhone(phone));
    }

    @When("A create request with phone {string} is passed to the add method")
    public void addPassengerMethodCalled(String phone) {
        DriverRequest createRequest = getDriverRequest();
        createRequest.setPhone(phone);
        try {
            driverResponse = driverService.add(createRequest);
        } catch (NotFoundException | AlreadyExistsException e) {
            exception = e;
        }
    }


    @Then("The response should contain created driver")
    public void responseContainsCreatedDriver() {
        var expected = getDefaultDriverResponse();
        assertEquals(driverResponse, expected);
    }

    @Then("The AlreadyExistsException should be thrown for phone {string}")
    public void notFoundExceptionThrown(String phone) {
        assertEquals(exception.getMessage(), String.format(DRIVER_WITH_PHONE_EXISTS_MESSAGE, phone));
    }

    @Given("A driver with id {long} exists when phone {string} doesn't exist")
    public void UpdateDriverWithUniqueData(long id, String phone) {
        Driver driverToUpdate = getUpdateDriver(phone);
        DriverResponse notSavedDriver = getNotSavedResponse(phone);
        Driver savedDriver = getSavedDriver(id, phone);
        doReturn(Optional.of(driverToUpdate))
                .when(driverRepository)
                .findByIdAndActiveIsTrue(id);
        doReturn(Optional.of(driverToUpdate))
                .when(driverRepository)
                .findById(id);
        doReturn(false)
                .when(driverRepository)
                .existsByPhone(phone);
        doReturn(driverToUpdate)
                .when(modelMapper)
                .map(any(DriverRequest.class), eq(Driver.class));
        doReturn(savedDriver)
                .when(driverRepository)
                .save(any(Driver.class));
        notSavedDriver.setId(id);
        doReturn(notSavedDriver)
                .when(modelMapper)
                .map(any(Driver.class), eq(DriverResponse.class));
        doReturn(getDefaultRatings())
                .when(ratingRepository)
                .getRatingsByDriverId(anyLong());
    }

    @When("An update request with phone {string} for driver with id {long} is passed to the update method")
    public void updateDriverMethodCalled(String phone, long id) {
        var request = getDriverRequest(phone);
        try {
            driverResponse = driverService.update(request, id);
        } catch (NotFoundException | AlreadyExistsException e) {
            exception = e;
        }
    }

    @Then("The response should contain updated driver with id {long}")
    public void responseContainsUpdatedDriver(long id) {
        DriverResponse actual = driverMapper.toDriverResponse(driverRepository.findByIdAndActiveIsTrue(id).get());

        assertEquals(actual, driverResponse);
    }

    @When("Driver id {long} is passed to the changeStatus method")
    public void idPassedToChangeStatusMethod(long id) {
        try {
            messageResponse = driverService.changeStatus(id);
        } catch (NotFoundException e) {
            exception = e;
        }
    }

    @Then("The response should contain status message with id {long}")
    public void responseContainsStatusMessage(long id) {
        MessageResponse actual = MessageResponse.builder()
                .message(String.format(EDIT_DRIVER_STATUS_MESSAGE, id))
                .build();

        assertEquals(actual, messageResponse);
    }

    @Given("A list of available drivers")
    public void givenAListOfAvailableDrivers() {
        Page<Driver> driverPage = new PageImpl<>(Arrays.asList(getDefaultDriver(), getSecondDriver()));
        doReturn(Optional.of(getDefaultDriver()))
                .when(driverRepository)
                .findById(anyLong());
        doReturn(driverPage)
                .when(driverRepository)
                .findByStatusAndActiveIsTrue(any(Status.class), any(PageRequest.class));
        doReturn(getDefaultDriverResponse())
                .when(modelMapper)
                .map(any(Driver.class), eq(DriverResponse.class));
    }

    @Given("A list of drivers")
    public void givenAListOfDrivers() {
        Page<Driver> driverPage = new PageImpl<>(Arrays.asList(getDefaultDriver(), getSecondDriver()));
        doReturn(Optional.of(getDefaultDriver()))
                .when(driverRepository)
                .findById(anyLong());
        doReturn(driverPage)
                .when(driverRepository)
                .findAll(any(PageRequest.class));
        doReturn(getDefaultDriverResponse())
                .when(modelMapper)
                .map(any(Driver.class), eq(DriverResponse.class));
    }

    @When("The findAvailableDrivers method is called with valid parameters")
    public void whenTheFindAvailableDriversMethodIsCalledWithValidParameters() {
        driversListResponse = driverService.findAvailableDrivers(VALID_PAGE, VALID_SIZE, VALID_ORDER_BY);
    }

    @When("The findAll method is called with valid parameters")
    public void whenTheFindAllMethodIsCalledWithValidParameters() {
        driversListResponse = driverService.findAll(VALID_PAGE, VALID_SIZE, VALID_ORDER_BY);
    }

    @Then("A list of drivers is returned")
    public void thenAListOfAvailableDriversIsReturned() {
        assertEquals(driversListResponse.getDrivers().size(), 2);
    }

    @When("The findAll method is called with invalid page")
    public void whenTheFindAllMethodIsCalledWithInValidPage() {
        try {
            driversListResponse = driverService.findAll(INVALID_PAGE, VALID_SIZE, VALID_ORDER_BY);
        } catch (InvalidRequestException e) {
            exception = e;
        }
    }

    @Then("The InvalidRequestException should be thrown for invalid page")
    public void invalidRequestExceptionWithPageThrow() {
        assertEquals(exception.getMessage(), INVALID_PAGE_MESSAGE);
    }
}

