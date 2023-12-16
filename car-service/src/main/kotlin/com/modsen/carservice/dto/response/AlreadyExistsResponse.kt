package com.modsen.carservice.dto.response

import org.springframework.http.HttpStatus

data class AlreadyExistsResponse(var status: HttpStatus, var message: String, var errors: Map<String, String?>)
