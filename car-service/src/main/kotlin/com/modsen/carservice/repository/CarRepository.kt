package com.modsen.carservice.repository

import com.modsen.carservice.dto.response.CarResponse
import com.modsen.carservice.entity.Car
import com.modsen.carservice.enum.Status
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CarRepository:JpaRepository<Car,Long> {
    fun existsByLicence(licence: String):Boolean
    fun existsByNumber(number:String):Boolean
    fun findByStatus(status: Status,pageRequest: PageRequest):Page<Car>
}