package com.modsen.carservice.exception.handler

import com.modsen.carservice.dto.response.AlreadyExistsResponse
import com.modsen.carservice.dto.response.ExceptionResponse
import com.modsen.carservice.dto.response.ValidationExceptionResponse
import com.modsen.carservice.exception.AlreadyExistsException
import com.modsen.carservice.exception.InvalidRequestException
import com.modsen.carservice.exception.NotFoundException
import com.modsen.carservice.util.Messages.VALIDATION_FAILED_MESSAGE
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.function.Consumer

@ControllerAdvice
class CarExceptionHandler {
    @ExceptionHandler(value = [AlreadyExistsException::class])
    fun handleAlreadyExistsException(alreadyExistsException: AlreadyExistsException): ResponseEntity<AlreadyExistsResponse> {
        val response = alreadyExistsException.message?.let {
            AlreadyExistsResponse(
                    HttpStatus.BAD_REQUEST,
                    it,alreadyExistsException.errors
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
    fun handleInvalidRequestException(invalidRequestException: InvalidRequestException): ResponseEntity<ExceptionResponse> {
        val response: ExceptionResponse? = invalidRequestException.message?.let {
            ExceptionResponse(HttpStatus.BAD_REQUEST,
                    it
            )
        }
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(methodArgumentNotValidException: MethodArgumentNotValidException): ResponseEntity<ValidationExceptionResponse> {
        val errors = HashMap<String, String?>()
        methodArgumentNotValidException.bindingResult.allErrors.forEach(Consumer { error: ObjectError ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage
            errors[fieldName] = errorMessage
        })
        val response = ValidationExceptionResponse(HttpStatus.BAD_REQUEST, VALIDATION_FAILED_MESSAGE, errors)
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }
}