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
    public DriverResponse createDriver(@RequestBody @Valid DriverRequest driverRequest) {
        return driverService.add(driverRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<MessageResponse> deleteDriver(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.delete(id));
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


