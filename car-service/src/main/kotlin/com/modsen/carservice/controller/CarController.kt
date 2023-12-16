package com.modsen.carservice.controller

import com.modsen.carservice.dto.request.CarRequest
import com.modsen.carservice.dto.response.CarResponse
import com.modsen.carservice.dto.response.CarsListResponse
import com.modsen.carservice.dto.response.MessageResponse
import com.modsen.carservice.entity.Car
import com.modsen.carservice.service.CarService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/cars")
class CarController(val carService: CarService) {

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    fun createCar(@RequestBody carRequest: CarRequest): ResponseEntity<CarResponse> {
        val response = carService.add(carRequest)
        return ResponseEntity.ok(response)
    }

    @GetMapping()
    fun findAll(
            @RequestParam(required = false, defaultValue = "1") page: Int,
            @RequestParam(required = false, defaultValue = "10") size: Int,
            @RequestParam(name = "order_by", required = false) orderBy: String?
    ): ResponseEntity<CarsListResponse> {
        val cars = carService.findAll(page, size, orderBy)
        return ResponseEntity.ok(cars)
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<CarResponse> {
        val response = carService.findById(id);
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<MessageResponse> {
        val response = carService.delete(id)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: CarRequest): ResponseEntity<CarResponse> {
        val response = carService.update(request, id)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/status/{id}")
    fun changeStatus(@PathVariable id: Long): ResponseEntity<MessageResponse> {
        val response = carService.changeStatus(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/available")
    fun availableCars(@RequestParam(required = false, defaultValue = "1") page: Int,
                      @RequestParam(required = false, defaultValue = "10") size: Int,
                      @RequestParam(name = "order_by", required = false) orderBy: String?): ResponseEntity<CarsListResponse> {
        val cars = carService.findAvailableCars(page, size, orderBy);
        return ResponseEntity.ok(cars)
    }
}