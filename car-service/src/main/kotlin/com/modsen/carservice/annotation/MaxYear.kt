package com.modsen.carservice.annotation

import com.modsen.carservice.validator.MaxYearValidator
import jakarta.validation.Constraint
import kotlin.reflect.KClass


@Target(AnnotationTarget.FIELD)
@Constraint(validatedBy = [MaxYearValidator::class])
annotation class MaxYear(val message: String = "Year should be less than current year", val groups: Array<KClass<*>> = [], val payload: Array<KClass<Any>> = [])
