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
    public PassengerResponse createPassenger(@RequestBody @Valid PassengerRequest passengerRequest) {
        return passengerService.add(passengerRequest);
    }

    @DeleteMapping("/{id}")
    public MessageResponse deletePassenger(@PathVariable Long id) {
        return passengerService.delete(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponse> passengerInfo(@PathVariable Long id) {
        PassengerResponse passenger = passengerService.findById(id);
        return ResponseEntity.ok(passenger);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PassengerResponse> updatePassenger(@PathVariable Long id, @RequestBody @Valid PassengerRequest passengerRequest) {
        PassengerResponse response = passengerService.update(passengerRequest, id);
        return ResponseEntity.ok(response);
    }


}
