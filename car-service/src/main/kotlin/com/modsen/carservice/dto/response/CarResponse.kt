package com.modsen.carservice.dto.response

import com.modsen.carservice.entity.Car
import com.modsen.carservice.enum.Status


data class CarResponse(
        val id: Long,
        val name: String,
        val number: String,
        val licence: String,
        val year: Int,
        val model: String,
        val status: Status
)
fun Car.toResponse() = CarResponse(
        id = id,
        name = name,
        number=number,
        licence=licence,
        year=year,
        model=model,
        status=status
)
