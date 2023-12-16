package com.modsen.carservice.dto.response

import org.springframework.http.HttpStatus


data class ValidationExceptionResponse(var status: HttpStatus, var message: String, var errors: Map<String, String?>)