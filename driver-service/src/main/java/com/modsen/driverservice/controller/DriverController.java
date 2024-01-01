package com.modsen.driverservice.controller;

import com.modsen.driverservice.dto.request.DriverRequest;
import com.modsen.driverservice.dto.response.DriverResponse;
import com.modsen.driverservice.dto.response.DriversListResponse;
import com.modsen.driverservice.dto.response.MessageResponse;
import com.modsen.driverservice.service.DriverService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drivers")
@Tag(name = "Driver Controller")
public class DriverController {
    private final DriverService driverService;

    @GetMapping
    public ResponseEntity<DriversListResponse> getAll(@RequestParam(required = false, defaultValue = "1") int page,
                                                      @RequestParam(required = false, defaultValue = "10") int size,
                                                      @RequestParam(name = "order_by", required = false) String orderBy) {
        DriversListResponse drivers = driverService.findAll(page, size, orderBy);
        return ResponseEntity.ok(drivers);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DriverResponse> createDriver(@RequestBody @Valid DriverRequest driverRequest) {
        DriverResponse response = driverService.add(driverRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteDriver(@PathVariable Long id) {
        driverService.delete(id);
        return ResponseEntity.ok(MessageResponse.builder().message("Deleting driver with id " + id).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> driverInfo(@PathVariable Long id) {
        DriverResponse passenger = driverService.findById(id);
        return ResponseEntity.ok(passenger);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponse> updateDriver(@PathVariable Long id, @RequestBody @Valid DriverRequest driverRequest) {
        DriverResponse response = driverService.update(driverRequest, id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<MessageResponse> changeStatus(@PathVariable Long id) {
        driverService.changeStatus(id);
        return ResponseEntity.ok(MessageResponse.builder().message("Status changed").build());
    }

    @GetMapping("/available")
    public ResponseEntity<DriversListResponse> getAvailable(@RequestParam(required = false, defaultValue = "1") int page,
                                                            @RequestParam(required = false, defaultValue = "10") int size,
                                                            @RequestParam(name = "order_by", required = false) String orderBy) {
        DriversListResponse drivers = driverService.findAvailableDrivers(page, size, orderBy);
        return ResponseEntity.ok(drivers);
    }
}


