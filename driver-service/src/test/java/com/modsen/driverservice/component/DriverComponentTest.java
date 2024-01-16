package com.modsen.driverservice.component;

import com.modsen.driverservice.dto.request.DriverRequest;
import com.modsen.driverservice.dto.response.DriverResponse;
import com.modsen.driverservice.dto.response.DriversListResponse;
import com.modsen.driverservice.dto.response.MessageResponse;
import com.modsen.driverservice.entity.Driver;
import com.modsen.driverservice.enums.Status;
import com.modsen.driverservice.exception.AlreadyExistsException;
import com.modsen.driverservice.exception.InvalidRequestException;
import com.modsen.driverservice.exception.NotFoundException;
import com.modsen.driverservice.mapper.DriverMapper;
import com.modsen.driverservice.repository.DriverRepository;
import com.modsen.driverservice.service.impl.DriverServiceImpl;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.RequiredArgsConstructor;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Optional;

import static com.modsen.driverservice.util.DriverTestUtils.*;
import static com.modsen.driverservice.util.Messages.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@CucumberContextConfiguration
public class DriverComponentTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private DriverMapper driverMapper;

    @InjectMocks
    private DriverServiceImpl driverService;

    private DriverResponse driverResponse;
    private Exception exception;
    private MessageResponse messageResponse;
    private DriversListResponse driversListResponse;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(DriverComponentTest.this);
    }

    @Given("A driver with id {long} exists")
    public void driverWithIdExists(long id) {
        DriverResponse expected = getDefaultDriverResponse();
        Driver retrievedDriver = getDefaultDriver();

        doReturn(Optional.of(retrievedDriver))
                .when(driverRepository)
                .findById(id);
        doReturn(true)
                .when(driverRepository)
                .existsById(id);
        doReturn(expected)
                .when(driverMapper)
                .toDriverResponse(retrievedDriver);

        Optional<Driver> driver = driverRepository.findById(id);
        assertThat(driver.isPresent()).isEqualTo(true);
    }

    @Given("A driver with id {long} doesn't exist")
    public void driverWithIdNotExist(long id) {
        Optional<Driver> driver = driverRepository.findById(id);
        assertThat(driver.isPresent()).isEqualTo(false);
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

        assertThat(driverResponse).isEqualTo(expected);
    }

    @Then("The NotFoundException with id {long} should be thrown")
    public void driverNotFoundExceptionThrown(long id) {
        String expected = String.format(NOT_FOUND_WITH_DRIVER_ID_MESSAGE, id);
        String actual = exception.getMessage();

        assertThat(actual).isEqualTo(expected);
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

        assertThat(messageResponse).isEqualTo(expected);
    }

    @Given("A driver with phone {string} doesn't exist")
    public void driverWithUniqueData(String phone) {
        DriverResponse expected = getDefaultDriverResponse();
        Driver driverToSave = getNotSavedDriver();
        Driver savedDriver = getDefaultDriver();

        doReturn(false)
                .when(driverRepository)
                .existsByPhone(phone);
        doReturn(driverToSave)
                .when(driverMapper)
                .toEntity(any(DriverRequest.class));
        doReturn(savedDriver)
                .when(driverRepository)
                .save(driverToSave);
        doReturn(expected)
                .when(driverMapper)
                .toDriverResponse(any(Driver.class));

    }


    @Given("A driver with phone {string} exists")
    public void driverWithPhoneExists(String phone) {
        doReturn(true)
                .when(driverRepository)
                .existsByPhone(phone);

        assertThat(driverRepository.existsByPhone(phone)).isEqualTo(true);
    }

    @When("A create request with phone {string} is passed to the add method")
    public void addPassengerMethodCalled(String phone) {
        DriverRequest createRequest = getDriverRequest();
        createRequest.setPhone(phone);
        try {
            driverResponse = driverService.add(createRequest);
        } catch (AlreadyExistsException e) {
            exception = e;
        }
    }


    @Then("The response should contain created driver")
    public void responseContainsCreatedDriver() {
        var expected = getDefaultDriverResponse();
        assertThat(driverResponse).isEqualTo(expected);
    }

    @Then("The AlreadyExistsException should be thrown for phone {string}")
    public void notFoundExceptionThrown(String phone) {
        assertThat(exception.getMessage()).isEqualTo(String.format(DRIVER_WITH_PHONE_EXISTS_MESSAGE, phone));
    }

    @Given("A driver with id {long} exists when phone {string} doesn't exist")
    public void UpdateDriverWithUniqueData(long id, String phone) {

        Driver driverToUpdate = Driver.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(phone)
                .build();
        DriverResponse notSavedDriver = DriverResponse.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(phone)
                .build();
        Driver savedDriver = Driver.builder()
                .id(id)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(phone)
                .build();
        doReturn(Optional.of(driverToUpdate))
                .when(driverRepository)
                .findById(id);
        doReturn(false)
                .when(driverRepository)
                .existsByPhone(phone);
        doReturn(driverToUpdate)
                .when(driverMapper)
                .toEntity(any(DriverRequest.class));
        doReturn(savedDriver)
                .when(driverRepository)
                .save(any(Driver.class));
        notSavedDriver.setId(id);
        doReturn(notSavedDriver)
                .when(driverMapper)
                .toDriverResponse(any(Driver.class));

    }

    @When("An update request with phone {string} for driver with id {long} is passed to the update method")
    public void updateDriverMethodCalled(String phone, long id) {
        var request = DriverRequest.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(phone)
                .build();
        try {
            driverResponse = driverService.update(request, id);
        } catch (NotFoundException | AlreadyExistsException e) {
            exception = e;
        }
    }

    @Then("The response should contain updated driver with id {long}")
    public void responseContainsUpdatedDriver(long id) {
        DriverResponse actual = driverMapper.toDriverResponse(driverRepository.findById(id).get());
        assertThat(actual).isEqualTo(driverResponse);
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
        assertThat(actual).isEqualTo(messageResponse);
    }

    @Given("A list of available drivers")
    public void givenAListOfAvailableDrivers() {
        Page<Driver> driverPage = new PageImpl<>(Arrays.asList(getDefaultDriver(), getSecondDriver()));
        when(driverRepository.findByStatus(any(Status.class), any(PageRequest.class))).thenReturn(driverPage);
        doReturn(getDefaultDriverResponse()).when(driverMapper).toDriverResponse(any(Driver.class));
    }

    @Given("A list of drivers")
    public void givenAListOfDrivers() {
        Page<Driver> driverPage = new PageImpl<>(Arrays.asList(getDefaultDriver(), getSecondDriver()));
        when(driverRepository.findAll(any(PageRequest.class))).thenReturn(driverPage);
        doReturn(getDefaultDriverResponse()).when(driverMapper).toDriverResponse(any(Driver.class));
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
        assertNotNull(driversListResponse);
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
        assertThat(exception.getMessage()).isEqualTo(INVALID_PAGE_MESSAGE);
    }
}

