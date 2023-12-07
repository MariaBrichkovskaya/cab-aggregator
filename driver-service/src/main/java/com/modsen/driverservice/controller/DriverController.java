package com.modsen.driverservice.controller;

import com.modsen.driverservice.dto.request.DriverRequest;
import com.modsen.driverservice.dto.response.DriverResponse;
import com.modsen.driverservice.service.DriverService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drivers")
@Tag(name = "Driver Controller")
public class DriverController {
    private final DriverService driverService;

    @GetMapping
    public ResponseEntity<List<DriverResponse>> getAll(@RequestParam(required = false, defaultValue = "1") int page,
                                                       @RequestParam(required = false, defaultValue = "10") int size,
                                                       @RequestParam(name = "order_by", required = false) String orderBy){
        List<DriverResponse> drivers=driverService.findAll(page,size,orderBy);
        return ResponseEntity.ok(drivers);
    }
    @PostMapping
    public ResponseEntity<String> createDriver(@RequestBody @Valid DriverRequest driverRequest) {
        driverService.add(driverRequest);
        return ResponseEntity.ok("Adding driver " + driverRequest.getName()+" "+driverRequest.getSurname());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDriver(@PathVariable Long id){
        driverService.delete(id);
        return ResponseEntity.ok("Deleting driver with id " + id);
    }
    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> driverInfo(@PathVariable Long id){
        DriverResponse passenger=driverService.findById(id);
        return ResponseEntity.ok(passenger);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateDriver(@PathVariable Long id,@RequestBody @Valid DriverRequest driverRequest)
    {
        driverService.update(driverRequest,id);
        return ResponseEntity.ok("Editing driver with id " + id);
    }
}


