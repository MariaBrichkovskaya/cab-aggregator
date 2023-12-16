package com.modsen.carservice.validator

import com.modsen.carservice.annotation.MaxYear
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.time.LocalDateTime


class MaxYearValidator : ConstraintValidator<MaxYear?, Int> {
    override fun initialize(constraintAnnotation: MaxYear?) {
    }

    override fun isValid(year: Int, context: ConstraintValidatorContext): Boolean {
        val currentYear = LocalDateTime.now().year
        return year <= currentYear
    }
}