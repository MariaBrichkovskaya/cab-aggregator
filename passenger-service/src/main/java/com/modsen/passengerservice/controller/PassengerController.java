package com.modsen.passengerservice.controller;


import com.modsen.passengerservice.dto.request.PassengerRequest;
import com.modsen.passengerservice.dto.response.MessageResponse;
import com.modsen.passengerservice.dto.response.PassengerResponse;
import com.modsen.passengerservice.dto.response.PassengersListResponse;
import com.modsen.passengerservice.service.PassengerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/passengers")
@Tag(name = "Passenger Controller")
public class PassengerController {
    private final PassengerService passengerService;

    @GetMapping
    public ResponseEntity<PassengersListResponse> getAll(@RequestParam(required = false, defaultValue = "1") int page,
                                                         @RequestParam(required = false, defaultValue = "10") int size,
                                                         @RequestParam(name = "order_by", required = false) String orderBy) {
        PassengersListResponse passengers = passengerService.findAll(page, size, orderBy);
        return ResponseEntity.ok(passengers);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_PASSENGER')")
    public PassengerResponse createPassenger(@AuthenticationPrincipal OAuth2User principal) {
        return passengerService.add(principal);
    }

    @PutMapping("/{id}/block")
    @PreAuthorize("hasAnyRole('ROLE_PASSENGER') && #id == authentication.principal.id")
    public MessageResponse deletePassenger(@PathVariable UUID id) {
        return passengerService.delete(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponse> passengerInfo(@PathVariable UUID id) {
        PassengerResponse passenger = passengerService.findById(id);
        return ResponseEntity.ok(passenger);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_PASSENGER')&& #id == authentication.principal.id")
    public ResponseEntity<PassengerResponse> updatePassenger(@PathVariable UUID id, @RequestBody @Valid PassengerRequest passengerRequest) {
        PassengerResponse response = passengerService.update(passengerRequest, id);
        return ResponseEntity.ok(response);
    }


}
