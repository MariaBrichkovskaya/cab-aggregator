package com.modsen.rideservice.service.impl;

import com.modsen.rideservice.client.DriverFeignClient;
import com.modsen.rideservice.client.PassengerFeignClient;
import com.modsen.rideservice.client.PaymentFeignClient;
import com.modsen.rideservice.dto.request.*;
import com.modsen.rideservice.dto.response.RideResponse;
import com.modsen.rideservice.dto.response.RidesListResponse;
import com.modsen.rideservice.entity.Ride;
import com.modsen.rideservice.enums.PaymentMethod;
import com.modsen.rideservice.enums.RideStatus;
import com.modsen.rideservice.exception.*;
import com.modsen.rideservice.kafka.RideProducer;
import com.modsen.rideservice.kafka.StatusProducer;
import com.modsen.rideservice.repository.RideRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Optional;

import static com.modsen.rideservice.util.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideServiceImplTest {
    @Mock
    private RideRepository rideRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RideServiceImpl rideService;
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

    @BeforeEach
    void setUp(TestInfo context) {
        if (context.getDisplayName().equals("findByIdWhenRideExists()")
                || context.getDisplayName().equals("updateWhenRideExists()")
                || context.getDisplayName().equals("findAllWhenParamsValid()")
                || context.getDisplayName().equals("editStatusWhenRideExistsDataIsValid()")
                || context.getDisplayName().equals("addWhenCustomerNotFound()")
                || context.getDisplayName().equals("addWhenBalanceInvalid()")
                || context.getDisplayName().equals("addWhenDataIsOkAndPaymentMethodIsCard()")
                || context.getDisplayName().equals("addWhenPaymentMethodIsCash()")) {
            doReturn(getDefaultDriverResponse()).when(driverFeignClient).getDriver(DEFAULT_ID);
            doReturn(getDefaultPassengerResponse()).when(passengerFeignClient).getPassenger(DEFAULT_ID);
        }
    }

    @AfterEach
    void tearDown(TestInfo context) {
        if (context.getDisplayName().equals("findByIdWhenRideExists()")
                || context.getDisplayName().equals("updateWhenRideExists()")
                || context.getDisplayName().equals("editStatusWhenRideExistsDataIsValid()")) {
            verify(driverFeignClient).getDriver(DEFAULT_ID);
            verify(passengerFeignClient).getPassenger(DEFAULT_ID);
        }
    }

    @AfterEach
    void tearDownForAddWhenMethodIsCard(TestInfo context) {
        if (context.getDisplayName().equals("addWhenCustomerNotFound()")
                || context.getDisplayName().equals("addWhenBalanceInvalid()")
                || context.getDisplayName().equals("addWhenDataIsOkAndPaymentMethodIsCard()")) {
            verify(driverFeignClient).getDriver(DEFAULT_ID);
            verify(passengerFeignClient, times(3)).getPassenger(DEFAULT_ID);
        }
    }


    @Test
    void sendEditStatusWhenRideNotFound() {
        DriverForRideRequest request = getDefaultDriverForRideRequest();
        doReturn(Optional.empty())
                .when(rideRepository)
                .findById(DEFAULT_ID);
        assertThrows(
                NotFoundException.class,
                () -> rideService.sendEditStatus(request)
        );
        verify(rideRepository).findById(DEFAULT_ID);
    }

    @Test
    void sendEditStatusWhenRideExists() {
        DriverForRideRequest request = getDefaultDriverForRideRequest();
        EditDriverStatusRequest statusRequest = EditDriverStatusRequest.builder()
                .driverId(request.driverId())
                .build();
        doReturn(Optional.of(getDefaultRide()))
                .when(rideRepository)
                .findById(DEFAULT_ID);

        rideService.sendEditStatus(request);

        verify(rideRepository).findById(DEFAULT_ID);
        verify(rideRepository).save(any(Ride.class));
        verify(statusProducer).sendMessage(statusRequest);
    }

    @Test
    void findByIdWhenRideNotFound() {
        doReturn(Optional.empty())
                .when(rideRepository)
                .findById(DEFAULT_ID);
        assertThrows(
                NotFoundException.class,
                () -> rideService.findById(DEFAULT_ID)
        );
        verify(rideRepository).findById(DEFAULT_ID);
    }

    @Test
    void findByIdWhenRideExists() {
        Ride ride = getDefaultRide();
        RideResponse expected = getDefaultRideResponse();
        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findById(DEFAULT_ID);
        doReturn(expected)
                .when(modelMapper)
                .map(ride, RideResponse.class);

        RideResponse actual = rideService.findById(DEFAULT_ID);

        assert (actual).equals(expected);
        verify(rideRepository).findById(DEFAULT_ID);
        verify(modelMapper).map(ride, RideResponse.class);
    }

    @Test
    void findAllWhenParamsValid() {
        Page<Ride> ridePage = new PageImpl<>(Arrays.asList(
                getDefaultRide(),
                getAlreadyFinishedRide()
        ));
        when(rideRepository.findAll(any(PageRequest.class))).thenReturn(ridePage);
        doReturn(getDefaultRideResponse()).when(modelMapper).map(any(Ride.class), eq(RideResponse.class));


        RidesListResponse response = rideService.findAll(VALID_PAGE, VALID_SIZE, VALID_ORDER_BY);

        assertNotNull(response);
        assertEquals(2, response.getRides().size());
        assertEquals(DEFAULT_ID, response.getRides().get(0).getId());
        verify(rideRepository).findAll(any(PageRequest.class));
        verify(modelMapper, times(2)).map(any(Ride.class), eq(RideResponse.class));
        verify(driverFeignClient, times(2)).getDriver(DEFAULT_ID);
        verify(passengerFeignClient, times(2)).getPassenger(DEFAULT_ID);
    }

    @Test
    void findAllWhenPageInvalid() {
        assertThrows(
                InvalidRequestException.class,
                () -> rideService.findAll(INVALID_PAGE, VALID_SIZE, VALID_ORDER_BY)
        );
    }

    @Test
    void findAllWhenSizeInvalid() {
        assertThrows(
                InvalidRequestException.class,
                () -> rideService.findAll(VALID_PAGE, INVALID_SIZE, VALID_ORDER_BY)
        );
    }

    @Test
    void findAllWhenOrderByInvalid() {
        assertThrows(
                InvalidRequestException.class,
                () -> rideService.findAll(VALID_PAGE, VALID_SIZE, INVALID_ORDER_BY)
        );
    }

    @Test
    void updateWhenRideNotFound() {
        doReturn(false)
                .when(rideRepository)
                .existsById(DEFAULT_ID);
        assertThrows(
                NotFoundException.class,
                () -> rideService.delete(DEFAULT_ID)
        );
        verify(rideRepository).existsById(DEFAULT_ID);
    }

    @Test
    void updateWhenRideExists() {
        Ride ride = getDefaultRideToSave();
        UpdateRideRequest updateRideRequest = getDefaultUpdateRideRequest();
        Ride savedRide = getDefaultRide();
        RideResponse response = getDefaultRideResponse();
        doReturn(true)
                .when(rideRepository)
                .existsById(DEFAULT_ID);
        doReturn(ride)
                .when(modelMapper)
                .map(updateRideRequest, Ride.class);
        doReturn(savedRide)
                .when(rideRepository)
                .save(ride);
        doReturn(response)
                .when(modelMapper)
                .map(savedRide, RideResponse.class);


        rideService.update(updateRideRequest, DEFAULT_ID);

        verify(rideRepository).existsById(DEFAULT_ID);
        verify(modelMapper).map(updateRideRequest, Ride.class);
        verify(rideRepository).save(ride);
        verify(modelMapper).map(savedRide, RideResponse.class);

    }

    @Test
    void deleteWhenRideNotFound() {
        doReturn(false)
                .when(rideRepository)
                .existsById(DEFAULT_ID);
        assertThrows(
                NotFoundException.class,
                () -> rideService.delete(DEFAULT_ID)
        );
        verify(rideRepository).existsById(DEFAULT_ID);
    }

    @Test
    void deleteWhenRideExists() {
        doReturn(true)
                .when(rideRepository)
                .existsById(DEFAULT_ID);

        rideService.delete(DEFAULT_ID);

        verify(rideRepository).deleteById(DEFAULT_ID);
    }


    @Test
    void getRidesByPassengerId() {
        Page<Ride> ridePage = new PageImpl<>(Arrays.asList(
                getDefaultRide(),
                getAlreadyFinishedRide()
        ));
        when(rideRepository.findAllByPassengerId(anyLong(), (any(PageRequest.class)))).thenReturn(ridePage);
        doReturn(getDefaultRideResponse()).when(modelMapper).map(any(Ride.class), eq(RideResponse.class));


        RidesListResponse response = rideService.getRidesByPassengerId(DEFAULT_ID, VALID_PAGE, VALID_SIZE, VALID_ORDER_BY);

        assertNotNull(response);
        assertEquals(2, response.getRides().size());
        assertEquals(DEFAULT_ID, response.getRides().get(0).getId());
        verify(rideRepository).findAllByPassengerId(anyLong(), (any(PageRequest.class)));
        verify(modelMapper, times(2)).map(any(Ride.class), eq(RideResponse.class));
        verify(driverFeignClient, times(2)).getDriver(DEFAULT_ID);
        verify(passengerFeignClient, times(2)).getPassenger(DEFAULT_ID);
    }

    @Test
    void getRidesByDriverId() {
        Page<Ride> ridePage = new PageImpl<>(Arrays.asList(
                getDefaultRide(),
                getAlreadyFinishedRide()
        ));
        when(rideRepository.findAllByDriverId(anyLong(), (any(PageRequest.class)))).thenReturn(ridePage);
        doReturn(getDefaultRideResponse()).when(modelMapper).map(any(Ride.class), eq(RideResponse.class));


        RidesListResponse response = rideService.getRidesByDriverId(DEFAULT_ID, VALID_PAGE, VALID_SIZE, VALID_ORDER_BY);

        assertNotNull(response);
        assertEquals(2, response.getRides().size());
        assertEquals(DEFAULT_ID, response.getRides().get(0).getId());
        verify(rideRepository).findAllByDriverId(anyLong(), (any(PageRequest.class)));
        verify(modelMapper, times(2)).map(any(Ride.class), eq(RideResponse.class));
        verify(driverFeignClient, times(2)).getDriver(DEFAULT_ID);
        verify(passengerFeignClient, times(2)).getPassenger(DEFAULT_ID);
    }


    @Test
    void editStatusWhenRideNotFound() {
        doReturn(Optional.empty())
                .when(rideRepository)
                .findById(DEFAULT_ID);
        assertThrows(
                NotFoundException.class,
                () -> rideService.editStatus(DEFAULT_ID, any(StatusRequest.class))
        );
        verify(rideRepository).findById(DEFAULT_ID);
    }

    @Test
    void editStatusWhenRideExistsAndDriverNotFound() {
        Ride ride = getDefaultRideWhereDriverNotAssign();
        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findById(DEFAULT_ID);
        assertThrows(
                DriverIsEmptyException.class,
                () -> rideService.editStatus(DEFAULT_ID, any(StatusRequest.class))
        );
        verify(rideRepository).findById(DEFAULT_ID);
    }

    @Test
    void editStatusWhenRideExistsAndRideAlreadyFinished() {
        Ride ride = getAlreadyFinishedRide();
        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findById(DEFAULT_ID);
        assertThrows(
                AlreadyFinishedRideException.class,
                () -> rideService.editStatus(DEFAULT_ID, any(StatusRequest.class))
        );
        verify(rideRepository).findById(DEFAULT_ID);
    }

    @Test
    void editStatusWhenRideExistsDataIsValid() {
        Ride ride = getDefaultRide();
        StatusRequest statusRequest = StatusRequest.builder()
                .status("FINISHED")
                .build();
        Ride finishedRide = getAlreadyFinishedRide();
        RideResponse response = getFinishedRideResponse();
        doReturn(Optional.of(ride))
                .when(rideRepository)
                .findById(DEFAULT_ID);
        doReturn(finishedRide).when(rideRepository).save(ride);
        doReturn(response)
                .when(modelMapper)
                .map(finishedRide, RideResponse.class);

        RideResponse expected = rideService.editStatus(DEFAULT_ID, statusRequest);

        assertEquals(expected.getRideStatus(), RideStatus.FINISHED);
        verify(statusProducer).sendMessage(any(EditDriverStatusRequest.class));
        verify(rideRepository).findById(DEFAULT_ID);
        verify(rideRepository).save(ride);
        verify(modelMapper).map(finishedRide, RideResponse.class);
    }


    @Test
    void addWhenPassengerNotFound() {
        CreateRideRequest request = getRideRequestWhitCard();
        Ride createdRide = getDefaultRideToSave();
        doReturn(createdRide).when(modelMapper).map(request, Ride.class);
        when(passengerFeignClient.getPassenger(DEFAULT_ID)).thenThrow(NotFoundException.class);
        assertThrows(
                InvalidRequestException.class,
                () -> rideService.add(request)
        );
        verify(modelMapper).map(request, Ride.class);
        verify(passengerFeignClient).getPassenger(DEFAULT_ID);

    }

    @Test
    void addWhenCustomerNotFound() {
        CreateRideRequest request = getRideRequestWhitCard();
        Ride createdRide = getDefaultRideToSave();
        RideResponse expected = getDefaultRideResponse();
        Ride savedRide = getDefaultRide();
        doReturn(createdRide).when(modelMapper).map(request, Ride.class);
        when(paymentFeignClient.findCustomer(DEFAULT_ID)).thenThrow(NotFoundException.class);
        doReturn(savedRide).when(rideRepository).save(createdRide);
        doReturn(expected).when(modelMapper).map(savedRide, RideResponse.class);

        RideResponse actual = rideService.add(request);

        assertNotNull(actual);
        assertEquals(actual, expected);
        verify(modelMapper).map(request, Ride.class);
        verify(paymentFeignClient).findCustomer(DEFAULT_ID);
        verify(paymentFeignClient).createCustomer(any(CustomerRequest.class));
        verify(paymentFeignClient).chargeFromCustomer(any(CustomerChargeRequest.class));
        verify(rideProducer).sendMessage(any(RideRequest.class));
        verify(modelMapper).map(savedRide, RideResponse.class);
    }

    @Test
    void addWhenBalanceInvalid() {
        CreateRideRequest request = getRideRequestWhitCard();
        Ride createdRide = getDefaultRideToSave();
        RideResponse expected = getDefaultRideResponse();
        expected.setPaymentMethod("CASH");
        Ride savedRide = getDefaultRide();
        doReturn(createdRide).when(modelMapper).map(request, Ride.class);
        when(paymentFeignClient.chargeFromCustomer(any(CustomerChargeRequest.class))).thenThrow(BalanceException.class);
        doReturn(savedRide).when(rideRepository).save(createdRide);
        doReturn(expected).when(modelMapper).map(savedRide, RideResponse.class);

        RideResponse actual = rideService.add(request);

        assertNotNull(actual);
        assertEquals(actual, expected);
        assertEquals(actual.getPaymentMethod(), "CASH");
        verify(modelMapper).map(request, Ride.class);
        verify(paymentFeignClient).findCustomer(DEFAULT_ID);
        verify(paymentFeignClient, times(0)).createCustomer(any(CustomerRequest.class));
        verify(paymentFeignClient).chargeFromCustomer(any(CustomerChargeRequest.class));
        verify(rideProducer).sendMessage(any(RideRequest.class));
        verify(modelMapper).map(savedRide, RideResponse.class);
    }

    @Test
    void addWhenDataIsOkAndPaymentMethodIsCard() {
        CreateRideRequest request = getRideRequestWhitCard();
        Ride createdRide = getDefaultRideToSave();
        RideResponse expected = getDefaultRideResponse();
        Ride savedRide = getDefaultRide();
        doReturn(createdRide).when(modelMapper).map(request, Ride.class);
        doReturn(savedRide).when(rideRepository).save(createdRide);
        doReturn(expected).when(modelMapper).map(savedRide, RideResponse.class);

        RideResponse actual = rideService.add(request);

        assertNotNull(actual);
        assertEquals(actual, expected);
        assertEquals(actual.getPaymentMethod(), "CARD");
        verify(modelMapper).map(request, Ride.class);
        verify(paymentFeignClient).findCustomer(DEFAULT_ID);
        verify(paymentFeignClient, times(0)).createCustomer(any(CustomerRequest.class));
        verify(paymentFeignClient).chargeFromCustomer(any(CustomerChargeRequest.class));
        verify(rideProducer).sendMessage(any(RideRequest.class));
        verify(modelMapper).map(savedRide, RideResponse.class);
    }

    @Test
    void addWhenPaymentMethodIsCash() {
        CreateRideRequest request = getRideRequestWhitCash();
        Ride createdRide = getDefaultRideToSave();
        createdRide.setPaymentMethod(PaymentMethod.CASH);
        RideResponse expected = getDefaultRideResponse();
        Ride savedRide = getDefaultRide();
        doReturn(createdRide).when(modelMapper).map(request, Ride.class);
        doReturn(savedRide).when(rideRepository).save(createdRide);
        doReturn(expected).when(modelMapper).map(savedRide, RideResponse.class);

        RideResponse actual = rideService.add(request);

        assertNotNull(actual);
        assertEquals(actual, expected);
        verify(modelMapper).map(request, Ride.class);
        verify(passengerFeignClient, times(2)).getPassenger(DEFAULT_ID);
        verify(paymentFeignClient, times(0)).findCustomer(DEFAULT_ID);
        verify(paymentFeignClient, times(0)).createCustomer(any(CustomerRequest.class));
        verify(paymentFeignClient, times(0)).chargeFromCustomer(any(CustomerChargeRequest.class));
        verify(rideProducer).sendMessage(any(RideRequest.class));
        verify(modelMapper).map(savedRide, RideResponse.class);
        verify(driverFeignClient).getDriver(DEFAULT_ID);
    }

}