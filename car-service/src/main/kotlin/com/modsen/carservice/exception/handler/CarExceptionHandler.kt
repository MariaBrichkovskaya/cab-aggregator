package com.modsen.carservice.exception.handler

import com.modsen.carservice.dto.response.ExceptionResponse
import com.modsen.carservice.exception.AlreadyExistsException
import com.modsen.carservice.exception.InvalidRequestException
import com.modsen.carservice.exception.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class CarExceptionHandler {
    @ExceptionHandler(value = [AlreadyExistsException::class])
    fun handleNotFoundException(alreadyExistsException: AlreadyExistsException): ResponseEntity<ExceptionResponse> {
        val response = alreadyExistsException.message?.let {
            ExceptionResponse(
                HttpStatus.BAD_REQUEST,
                    it
        )
        }
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler(value = [NotFoundException::class])
    fun handleNotFoundException(notFoundException: NotFoundException): ResponseEntity<ExceptionResponse> {
        val response: ExceptionResponse? = notFoundException.message?.let {
            ExceptionResponse(HttpStatus.NOT_FOUND,
                    it
        )
        }
        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(value = [InvalidRequestException::class])
    fun handleNotFoundException(invalidRequestException: InvalidRequestException): ResponseEntity<ExceptionResponse> {
        val response: ExceptionResponse? = invalidRequestException.message?.let {
            ExceptionResponse(HttpStatus.BAD_REQUEST,
                    it
        )
        }
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }
}