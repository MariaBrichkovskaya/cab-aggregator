package com.modsen.carservice.annotation

import com.modsen.carservice.validator.MaxYearValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.KClass


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [MaxYearValidator::class])
annotation class MaxYear(
        val message: String = "Year should be less than current",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
)