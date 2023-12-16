package com.modsen.carservice.service

import com.modsen.carservice.dto.request.CarRequest
import com.modsen.carservice.dto.response.CarResponse
import com.modsen.carservice.dto.response.CarsListResponse
import com.modsen.carservice.dto.response.MessageResponse

interface CarService {
    fun add(request: CarRequest): CarResponse
    fun findById(id: Long): CarResponse
    fun findAll(page: Int, size: Int, sortingParam: String?): CarsListResponse
    fun update(request: CarRequest, id: Long): CarResponse
    fun delete(id: Long): MessageResponse
    fun changeStatus(id: Long): MessageResponse
    fun findAvailableCars(page: Int, size: Int, sortingParam: String?): CarsListResponse
}