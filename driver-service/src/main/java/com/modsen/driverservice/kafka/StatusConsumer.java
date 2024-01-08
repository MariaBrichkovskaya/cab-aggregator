package com.modsen.driverservice.kafka;

import com.modsen.driverservice.dto.request.DriverForRideRequest;
import com.modsen.driverservice.dto.request.EditDriverStatusRequest;
import com.modsen.driverservice.dto.response.DriverResponse;
import com.modsen.driverservice.enums.Status;
import com.modsen.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class StatusConsumer {
    private final DriverService driverService;
    private final DriverProducer driverProducer;

    @KafkaListener(topics = "${topic.name.status}", groupId = "${spring.kafka.consumer.group-id.status}", containerFactory = "statusKafkaListenerContainerFactory")
    public void consumeMessage(EditDriverStatusRequest message) {
        log.info("message consumed {}", message);
        editStatus(message);
    }

    private void editStatus(EditDriverStatusRequest editDriverStatusRequest) {
        driverService.changeStatus(editDriverStatusRequest.driverId());
        DriverResponse driver = driverService.findById(editDriverStatusRequest.driverId());
        if (driver.getStatus().equals(Status.AVAILABLE)) {
            driverProducer.sendMessage(DriverForRideRequest.builder()
                    .driverId(driver.getId())
                    .build()
            );
        }
    }

}
