package com.modsen.rideservice.controller;

import com.modsen.rideservice.dto.request.CreateRideRequest;
import com.modsen.rideservice.dto.request.StatusRequest;
import com.modsen.rideservice.dto.request.UpdateRideRequest;
import com.modsen.rideservice.dto.response.MessageResponse;
import com.modsen.rideservice.dto.response.RideResponse;
import com.modsen.rideservice.dto.response.RidesListResponse;
import com.modsen.rideservice.service.RideService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

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
    @PreAuthorize("hasAnyRole('ROLE_PASSENGER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RideResponse> createRide(@RequestBody @Valid CreateRideRequest rideRequest,
                                                   @AuthenticationPrincipal OAuth2User principal) {
        RideResponse response = rideService.add(rideRequest, principal);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public MessageResponse deleteRide(@PathVariable Long id) {
        return rideService.delete(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideResponse> rideInfo(@PathVariable Long id) {
        RideResponse ride = rideService.findById(id);
        return ResponseEntity.ok(ride);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<RideResponse> updateRide(@PathVariable Long id, @RequestBody @Valid UpdateRideRequest rideRequest) {
        RideResponse response = rideService.update(rideRequest, id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/passenger/{passenger_id}")
    @PreAuthorize("hasAnyRole('ROLE_PASSENGER')&& #passengerId == authentication.principal.id")
    public ResponseEntity<RidesListResponse> passengerRides(@PathVariable(name = "passenger_id") UUID passengerId, @RequestParam(required = false, defaultValue = "1") int page,
                                                            @RequestParam(required = false, defaultValue = "10") int size,
                                                            @RequestParam(name = "order_by", required = false) String orderBy) {
        RidesListResponse rides = rideService.getRidesByPassengerId(passengerId, page, size, orderBy);
        return ResponseEntity.ok(rides);
    }

    @GetMapping("/driver/{driver_id}")
    @PreAuthorize("hasAnyRole('ROLE_DRIVER')&& #driverId == authentication.principal.id")
    public ResponseEntity<RidesListResponse> driverRides(@PathVariable(name = "driver_id") UUID driverId, @RequestParam(required = false, defaultValue = "1") int page,
                                                         @RequestParam(required = false, defaultValue = "10") int size,
                                                         @RequestParam(name = "order_by", required = false) String orderBy) {
        RidesListResponse rides = rideService.getRidesByDriverId(driverId, page, size, orderBy);
        return ResponseEntity.ok(rides);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ROLE_DRIVER')")
    public ResponseEntity<RideResponse> editStatus(@PathVariable Long id, @RequestBody @Valid StatusRequest statusRequest) {
        RideResponse response = rideService.editStatus(id, statusRequest);
        return ResponseEntity.ok(response);
    }
}
