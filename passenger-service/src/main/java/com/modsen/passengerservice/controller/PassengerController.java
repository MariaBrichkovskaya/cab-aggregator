package com.modsen.passengerservice.controller;

import com.modsen.passengerservice.dto.request.PassengerRequest;
import com.modsen.passengerservice.dto.request.PassengerRatingRequest;
import com.modsen.passengerservice.dto.response.MessageResponse;
import com.modsen.passengerservice.dto.response.PassengerResponse;
import com.modsen.passengerservice.dto.response.PassengersListResponse;
import com.modsen.passengerservice.service.PassengerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<MessageResponse> createPassenger(@RequestBody @Valid PassengerRequest passengerRequest) {
        passengerService.add(passengerRequest);
        String message="Adding passenger " + passengerRequest.getName() + " " + passengerRequest.getSurname();
        return ResponseEntity.ok(MessageResponse.builder().message(message).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deletePassenger(@PathVariable Long id) {
        passengerService.delete(id);
        String message="Deleting passenger with id " + id;
        return ResponseEntity.ok(MessageResponse.builder().message(message).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponse> passengerInfo(@PathVariable Long id) {
        PassengerResponse passenger = passengerService.findById(id);
        return ResponseEntity.ok(passenger);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updatePassenger(@PathVariable Long id, @RequestBody @Valid PassengerRequest passengerRequest) {
        passengerService.update(passengerRequest, id);
        String message="Editing passenger with id " + id;
        return ResponseEntity.ok(MessageResponse.builder().message(message).build());
    }


}
