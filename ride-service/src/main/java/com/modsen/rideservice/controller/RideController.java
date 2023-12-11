package com.modsen.rideservice.controller;

import com.modsen.rideservice.dto.request.RideRequest;
import com.modsen.rideservice.dto.request.StatusRequest;
import com.modsen.rideservice.dto.response.MessageResponse;
import com.modsen.rideservice.dto.response.RideResponse;
import com.modsen.rideservice.dto.response.RidesListResponse;
import com.modsen.rideservice.service.RideService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rides")
@Tag(name = "Ride Controller")
@RequiredArgsConstructor
public class RideController {
    private final RideService rideService;

    @GetMapping
    public ResponseEntity<RidesListResponse> getAll(@RequestParam(required = false, defaultValue = "1") int page,
                                                    @RequestParam(required = false, defaultValue = "10") int size,
                                                    @RequestParam(name = "order_by", required = false) String orderBy) {
        RidesListResponse rides = rideService.findAll(page, size, orderBy);
        return ResponseEntity.ok(rides);
    }

    @PostMapping
    public ResponseEntity<MessageResponse> createRide(@RequestBody @Valid RideRequest rideRequest) {
        rideService.add(rideRequest);
        return ResponseEntity.ok(MessageResponse.builder().message("Adding ride").build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteRide(@PathVariable Long id) {
        rideService.delete(id);
        return ResponseEntity.ok(MessageResponse.builder().message("Deleting ride with id " + id).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideResponse> rideInfo(@PathVariable Long id) {
        RideResponse ride = rideService.findById(id);
        return ResponseEntity.ok(ride);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateRide(@PathVariable Long id, @RequestBody @Valid RideRequest rideRequest) {
        rideService.update(rideRequest, id);
        return ResponseEntity.ok(MessageResponse.builder().message("Editing ride with id " + id).build());
    }

    @GetMapping("/passenger/{passenger_id}")
    public ResponseEntity<RidesListResponse> passengerRides(@PathVariable(name = "passenger_id") long passengerId, @RequestParam(required = false, defaultValue = "1") int page,
                                                            @RequestParam(required = false, defaultValue = "10") int size,
                                                            @RequestParam(name = "order_by", required = false) String orderBy) {
        RidesListResponse rides = rideService.getRidesByPassengerId(passengerId, page, size, orderBy);
        return ResponseEntity.ok(rides);
    }

    @GetMapping("/driver/{driver_id}")
    public ResponseEntity<RidesListResponse> driverRides(@PathVariable(name = "driver_id") long driverId, @RequestParam(required = false, defaultValue = "1") int page,
                                                         @RequestParam(required = false, defaultValue = "10") int size,
                                                         @RequestParam(name = "order_by", required = false) String orderBy) {
        RidesListResponse rides = rideService.getRidesByDriverId(driverId, page, size, orderBy);
        return ResponseEntity.ok(rides);
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<MessageResponse> editStatus(@PathVariable Long id, @RequestBody @Valid StatusRequest statusRequest) {
        rideService.editStatus(id, statusRequest);
        return ResponseEntity.ok(MessageResponse.builder().message("Status updated to " + statusRequest.getStatus()).build());
    }
}
