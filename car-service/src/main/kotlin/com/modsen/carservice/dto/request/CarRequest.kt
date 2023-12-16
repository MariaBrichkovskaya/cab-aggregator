package com.modsen.carservice.dto.request

import com.modsen.carservice.annotation.MaxYear
import com.modsen.carservice.entity.Car
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull


data class CarRequest(
        @field:NotBlank(message = "Number cannot be blank")
        val number: String,
        @field:NotBlank(message = "Licence cannot be blank")
        val licence: String,
        @field:NotBlank(message = "Name cannot be blank")
        val name: String,
        @field:Min(value = 1900, message = "Year should be greater than 1900")
        @field:MaxYear
        @field:NotNull(message = "year cannot be empty")
        val year: Int,
        @field:NotBlank(message = "Model cannot be blank")
        val model: String
)

fun CarRequest.toEntity() = Car(
        name = name,
        number = number,
        licence = licence,
        year = year,
        model = model
)
