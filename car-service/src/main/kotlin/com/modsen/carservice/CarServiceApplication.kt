package com.modsen.carservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CarServiceApplication

fun main(args: Array<String>) {
    runApplication<CarServiceApplication>(*args)
}
