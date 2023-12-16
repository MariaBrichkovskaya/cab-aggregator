package com.modsen.carservice.dto.response

import org.springframework.http.HttpStatus



data class ExceptionResponse(var status: HttpStatus, var message: String) {
}

