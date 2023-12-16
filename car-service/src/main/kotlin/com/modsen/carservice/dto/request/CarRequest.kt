package com.modsen.carservice.dto.request

import com.modsen.carservice.entity.Car


data class CarRequest(
        val number:String,
        val licence:String,
        val name:String,
        val year: Int,
        val model:String
)
fun CarRequest.toEntity() = Car(
        name = name,
        number=number,
        licence = licence,
        year = year,
        model = model
)
