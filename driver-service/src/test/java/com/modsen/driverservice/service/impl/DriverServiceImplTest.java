package com.modsen.driverservice.service.impl;

import com.modsen.driverservice.dto.request.DriverForRideRequest;
import com.modsen.driverservice.dto.request.DriverRequest;
import com.modsen.driverservice.dto.request.RideRequest;
import com.modsen.driverservice.dto.response.DriverResponse;
import com.modsen.driverservice.dto.response.DriversListResponse;
import com.modsen.driverservice.entity.Driver;
import com.modsen.driverservice.enums.Status;
import com.modsen.driverservice.exception.AlreadyExistsException;
import com.modsen.driverservice.exception.InvalidRequestException;
import com.modsen.driverservice.exception.NotFoundException;
import com.modsen.driverservice.kafka.DriverProducer;
import com.modsen.driverservice.mapper.DriverMapper;
import com.modsen.driverservice.repository.DriverRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static com.modsen.driverservice.util.DriverTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverServiceImplTest {
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private DriverMapper driverMapper;

    @InjectMocks
    private DriverServiceImpl driverService;
    @Mock
    private DriverProducer driverProducer;

    @Test
    void addDriverWhenDriverUnique() {
        DriverResponse expected = getDefaultDriverResponse();
        Driver notSavedDriver = getNotSavedDriver();
        Driver savedDriver = getDefaultDriver();
        DriverRequest createRequest = getDriverRequest();
        doReturn(false)
                .when(driverRepository)
                .existsByPhone(DEFAULT_PHONE);
        doReturn(notSavedDriver)
                .when(driverMapper)
                .toEntity(createRequest);
        doReturn(savedDriver)
                .when(driverRepository)
                .save(notSavedDriver);
        doReturn(expected)
                .when(driverMapper)
                .toDriverResponse(savedDriver);

        DriverResponse actual = driverService.add(createRequest);

        verify(driverRepository).existsByPhone(DEFAULT_PHONE);
        verify(driverRepository).save(notSavedDriver);
        verify(driverMapper).toEntity(createRequest);
        verify(driverMapper).toDriverResponse(savedDriver);
        assertThat(actual).isEqualTo(expected);

    }

    @Test
    void addDriverWhenPhoneIsNotUnique() {
        DriverRequest createRequest = getDriverRequest();
        doReturn(true)
                .when(driverRepository)
                .existsByPhone(DEFAULT_PHONE);
        assertThrows(
                AlreadyExistsException.class,
                () -> driverService.add(createRequest)
        );
        verify(driverRepository).existsByPhone(DEFAULT_PHONE);
    }


    @Test
    void findByIdDriverShouldExist() {
        Driver retrievedDriver = getDefaultDriver();
        DriverResponse expected = getDefaultDriverResponse();
        doReturn(Optional.of(retrievedDriver))
                .when(driverRepository)
                .findById(DEFAULT_ID);
        doReturn(expected)
                .when(driverMapper)
                .toDriverResponse(retrievedDriver);


        DriverResponse actual = driverService.findById(DEFAULT_ID);

        verify(driverRepository).findById(DEFAULT_ID);
        verify(driverMapper).toDriverResponse(retrievedDriver);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findByIdPassengerNotFound() {
        doReturn(Optional.empty())
                .when(driverRepository)
                .findById(DEFAULT_ID);
        assertThrows(
                NotFoundException.class,
                () -> driverService.findById(DEFAULT_ID)
        );
        verify(driverRepository).findById(DEFAULT_ID);
    }


    @Test
    void findAllWhenPageInvalid() {
        assertThrows(
                InvalidRequestException.class,
                () -> driverService.findAll(INVALID_PAGE, VALID_SIZE, VALID_ORDER_BY)
        );
    }

    @Test
    void findAllWhenSizeInvalid() {
        assertThrows(
                InvalidRequestException.class,
                () -> driverService.findAll(VALID_PAGE, INVALID_SIZE, VALID_ORDER_BY)
        );
    }

    @Test
    void findAllWhenOrderByInvalid() {
        assertThrows(
                InvalidRequestException.class,
                () -> driverService.findAll(VALID_PAGE, VALID_SIZE, INVALID_ORDER_BY)
        );
    }

    @Test
    void updateWhenDriverNotFound() {
        doReturn(Optional.empty())
                .when(driverRepository)
                .findById(DEFAULT_ID);
        DriverRequest passengerRequest = getDriverRequest();
        assertThrows(
                NotFoundException.class,
                () -> driverService.update(passengerRequest, DEFAULT_ID)
        );
        verify(driverRepository).findById(DEFAULT_ID);
    }


    @Test
    void deleteWhenDriverNotFound() {
        doReturn(false)
                .when(driverRepository)
                .existsById(DEFAULT_ID);
        assertThrows(
                NotFoundException.class,
                () -> driverService.delete(DEFAULT_ID)
        );
        verify(driverRepository).existsById(DEFAULT_ID);
    }

    @Test
    void deleteWhenDriverExists() {
        doReturn(true)
                .when(driverRepository)
                .existsById(DEFAULT_ID);

        driverService.delete(DEFAULT_ID);

        verify(driverRepository).deleteById(DEFAULT_ID);
    }

    @Test
    void findAllWhenParamsValid() {

        Page<Driver> driverPagePage = new PageImpl<>(Arrays.asList(
                getDefaultDriver(),
                getSecondDriver()
        ));
        when(driverRepository.findAll(any(PageRequest.class))).thenReturn(driverPagePage);
        doReturn(getDefaultDriverResponse()).when(driverMapper).toDriverResponse(any(Driver.class));

        DriversListResponse response = driverService.findAll(VALID_PAGE, VALID_SIZE, VALID_ORDER_BY);

        assertNotNull(response);
        assertEquals(2, response.getDrivers().size());
        assertEquals(DEFAULT_ID, response.getDrivers().get(0).getId());
        assertEquals(DEFAULT_NAME, response.getDrivers().get(0).getName());
        verify(driverRepository).findAll(any(PageRequest.class));
        verify(driverMapper, times(2)).toDriverResponse(any(Driver.class));
    }

    @Test
    void updateWhenDriverDataIsNotUnique() {
        Driver updateDriver = getUpdateDriver();
        DriverRequest request = getDriverRequest();
        doReturn(Optional.of(updateDriver)).when(driverRepository).findById(DEFAULT_ID);
        doReturn(true).when(driverRepository).existsByPhone(request.getPhone());

        assertThrows(
                AlreadyExistsException.class,
                () -> driverService.update(request, DEFAULT_ID)
        );
        verify(driverRepository).findById(DEFAULT_ID);
        verify(driverRepository).existsByPhone(request.getPhone());
    }

    @Test
    void updateWhenDriverExistsAndPhoneIsUnique() {
        Driver updateDriver = getUpdateDriver();
        DriverRequest request = getDriverRequest();
        DriverResponse response = getDefaultDriverResponse();
        when(driverRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(updateDriver));
        when(driverRepository.existsByPhone(request.getPhone())).thenReturn(false);
        when(driverMapper.toEntity(request)).thenReturn(updateDriver);
        when(driverRepository.save(updateDriver)).thenReturn(updateDriver);
        when(driverMapper.toDriverResponse(updateDriver)).thenReturn(response);

        DriverResponse result = driverService.update(request, DEFAULT_ID);

        verify(driverRepository).findById(DEFAULT_ID);
        verify(driverRepository).existsByPhone(request.getPhone());
        verify(driverMapper).toEntity(request);
        verify(driverRepository).save(updateDriver);
        verify(driverMapper).toDriverResponse(updateDriver);
        assertThat(result).isNotNull();
        assertThat(result.getPhone()).isEqualTo(request.getPhone());
    }

    @Test
    void changeStatusWhenDriverNotFound() {
        doReturn(Optional.empty())
                .when(driverRepository)
                .findById(DEFAULT_ID);

        assertThrows(
                NotFoundException.class,
                () -> driverService.changeStatus(DEFAULT_ID)
        );
        verify(driverRepository).findById(DEFAULT_ID);
    }

    @Test
    void changeStatusWhenDriverExists() {
        Driver driver = getDefaultDriver();
        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findById(DEFAULT_ID);

        driverService.changeStatus(DEFAULT_ID);

        verify(driverRepository).findById(DEFAULT_ID);
        verify(driverRepository, times(1)).save(driver);
        assertEquals(Status.UNAVAILABLE, driver.getStatus());
    }

    @Test
    void findAvailableDrivers() {
        Page<Driver> passengerPage = new PageImpl<>(Arrays.asList(
                getDefaultDriver(),
                getSecondDriver()
        ));
        when(driverRepository.findByStatus(any(Status.class), any(PageRequest.class))).thenReturn(passengerPage);
        doReturn(getDefaultDriverResponse()).when(driverMapper).toDriverResponse(any(Driver.class));

        DriversListResponse response = driverService.findAvailableDrivers(VALID_PAGE, VALID_SIZE, VALID_ORDER_BY);

        assertNotNull(response);
        assertEquals(2, response.getDrivers().size());
        assertEquals(DEFAULT_ID, response.getDrivers().get(0).getId());
        assertEquals(DEFAULT_NAME, response.getDrivers().get(0).getName());
        verify(driverRepository).findByStatus(any(Status.class), any(PageRequest.class));
        verify(driverMapper, times(2)).toDriverResponse(any(Driver.class));
    }


    @Test
    void findDriverForRideWhenDriversAvailable() {
        RideRequest request = getDefaultRideRequest();
        Page<Driver> driverPage = new PageImpl<>(Arrays.asList(
                getDefaultDriver(),
                getSecondDriver()
        ));
        when(driverRepository.findByStatus(any(Status.class), any(PageRequest.class))).thenReturn(driverPage);
        doReturn(getDefaultDriverResponse())
                .when(driverMapper)
                .toDriverResponse(any(Driver.class));

        driverService.findDriverForRide(request);

        verify(driverRepository).findByStatus(any(Status.class), any(PageRequest.class));
        verify(driverMapper, times(2)).toDriverResponse(any(Driver.class));
        verify(driverProducer).sendMessage(any(DriverForRideRequest.class));
    }

    @Test
    void findDriverForRideWhenDriversNotAvailable() {
        RideRequest request = getDefaultRideRequest();
        when(driverRepository.findByStatus(any(Status.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        driverService.findDriverForRide(request);

        verify(driverRepository).findByStatus(any(Status.class), any(PageRequest.class));
        verify(driverMapper, never()).toDriverResponse(any(Driver.class));
        verify(driverProducer, never()).sendMessage(any(DriverForRideRequest.class));
    }
}