package com.modsen.rideservice.kafka;

import com.modsen.rideservice.dto.request.DriverForRideRequest;
import com.modsen.rideservice.dto.request.EditDriverStatusRequest;
import com.modsen.rideservice.entity.Ride;
import com.modsen.rideservice.enums.RideStatus;
import com.modsen.rideservice.repository.RideRepository;
import com.modsen.rideservice.service.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class DriverConsumer {
    private final RideService rideService;
    private final RideRepository rideRepository;
    private final StatusProducer statusProducer;

    @KafkaListener(topics = "${topic.name.driver}", groupId = "${spring.kafka.consumer.group-id.driver}")
    public void consumeMessage(DriverForRideRequest driver) {
        log.info("message consumed {}", driver);
        if (driver.getRideId() == 0) {
            setDriver(driver);
        } else {
            rideService.setDriver(driver);
        }

    }

    private void setDriver(DriverForRideRequest driver) {
        if (!rideRepository.findAll().stream().filter(ride -> ride.getDriverId() == null).toList().isEmpty()) {
            Ride ride = rideRepository.findAll().stream().filter(rideWithoutDriver -> rideWithoutDriver.getDriverId() == null).toList().get(0);
            ride.setDriverId(driver.getDriverId());
            ride.setRideStatus(RideStatus.ACCEPTED);
            rideRepository.save(ride);
            statusProducer.sendMessage(EditDriverStatusRequest.builder().driverId(driver.getDriverId()).build());
        }
    }
}
