package com.modsen.carservice.service.impl

import com.modsen.carservice.dto.request.CarRequest
import com.modsen.carservice.dto.request.toEntity
import com.modsen.carservice.dto.response.CarResponse
import com.modsen.carservice.dto.response.CarsListResponse
import com.modsen.carservice.dto.response.MessageResponse
import com.modsen.carservice.dto.response.toResponse
import com.modsen.carservice.entity.Car
import com.modsen.carservice.enum.Status
import com.modsen.carservice.exception.AlreadyExistsException
import com.modsen.carservice.exception.InvalidRequestException
import com.modsen.carservice.exception.NotFoundException
import com.modsen.carservice.repository.CarRepository
import com.modsen.carservice.service.CarService
import com.modsen.carservice.util.Messages.CAR_WITH_LICENCE_EXISTS_MESSAGE
import com.modsen.carservice.util.Messages.CAR_WITH_NUMBER_EXISTS_MESSAGE
import com.modsen.carservice.util.Messages.INVALID_PAGE_MESSAGE
import com.modsen.carservice.util.Messages.INVALID_SORTING_MESSAGE
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.reflect.Field
import java.util.*
import java.util.function.Function


@Service
@Transactional
class CarServiceImpl(private val carRepository: CarRepository) : CarService {
    override fun add(request: CarRequest): CarResponse {
        checkCreateDataIsUnique(request)
        val car = carRepository.save(request.toEntity())
        return car.toResponse()
    }

    private fun checkNumberIsUnique(number: String, errors: MutableMap<String, String>) {
        if (carRepository.existsByNumber(number)) {
            errors["number"] = String.format(CAR_WITH_NUMBER_EXISTS_MESSAGE, number)
        }
    }

    private fun checkLicenceIsUnique(licence: String, errors: MutableMap<String, String>) {
        if (carRepository.existsByLicence(licence)) {
            errors["licence"] = String.format(CAR_WITH_LICENCE_EXISTS_MESSAGE, licence)
        }
    }

    private fun checkCreateDataIsUnique(request: CarRequest) {
        val errors = HashMap<String, String>()
        checkLicenceIsUnique(request.licence, errors)
        checkNumberIsUnique(request.number, errors)
        if (errors.isNotEmpty()) {
            throw AlreadyExistsException(errors)
        }
    }

    @Transactional(readOnly = true)
    override fun findById(id: Long): CarResponse {
        val car = carRepository.findById(id).orElseThrow { NotFoundException(id) }
        return car.toResponse()
    }

    @Transactional(readOnly = true)
    override fun findAll(page: Int, size: Int, sortingParam: String?): CarsListResponse {
        val pageRequest = getPageRequest(page, size, sortingParam)
        val carsPage = carRepository.findAll(pageRequest)
        val cars = carsPage.content.map { it.toResponse() }
        return CarsListResponse(cars = cars)
    }

    private fun getPageRequest(page: Int, size: Int, sortingParam: String?): PageRequest {
        if (page < 1 || size < 1) {
            throw InvalidRequestException(INVALID_PAGE_MESSAGE)
        }
        val pageRequest: PageRequest = if (sortingParam == null) {
            PageRequest.of(page - 1, size, Sort.by("id"))
        } else {
            validateSortingParameter(sortingParam)
            PageRequest.of(page - 1, size, Sort.by(sortingParam))
        }
        return pageRequest
    }

    private fun validateSortingParameter(sortingParam: String) {
        val fieldNames = Arrays.stream(CarResponse::class.java.getDeclaredFields())
                .map { obj: Field -> obj.name }
                .toList()
        if (!fieldNames.contains(sortingParam)) {
            val errorMessage = String.format(INVALID_SORTING_MESSAGE, fieldNames)
            throw InvalidRequestException(errorMessage)
        }
    }

    override fun update(request: CarRequest, id: Long): CarResponse {
        val carToUpdate: Car = carRepository.findById(id).orElseThrow{NotFoundException(id)}
        checkUpdateDataIsUnique(request, carToUpdate)
        val car:Car=request.toEntity()
        car.id = id
        return carRepository.save(car).toResponse()
    }

    private fun checkUpdateDataIsUnique(request: CarRequest, car: Car) {
        val errors = HashMap<String, String>()
        if (request.licence != car.licence) {
            checkLicenceIsUnique(request.licence, errors)
        }
        if (request.number != car.number) {
            checkNumberIsUnique(request.number, errors)
        }
        if (errors.isNotEmpty()) {
            throw AlreadyExistsException(errors)
        }
    }

    override fun delete(id: Long): MessageResponse {
        val car = carRepository.findById(id).orElseThrow { NotFoundException(id) }
        carRepository.delete(car)
        return MessageResponse(message = "Deleted car with id $id")
    }

    override fun changeStatus(id: Long): MessageResponse {
        val car: Car = carRepository.findById(id).orElseThrow { NotFoundException(id) }
        if (car.status == Status.AVAILABLE) {
            car.status = Status.UNAVAILABLE
        } else {
            car.status = Status.AVAILABLE
        }
        carRepository.save(car)
        val message = "Status changed to ${car.status}"
        return MessageResponse(message)
    }

    @Transactional(readOnly = true)
    override fun findAvailableCars(page: Int, size: Int, sortingParam: String?): CarsListResponse {
        val pageRequest = getPageRequest(page, size, sortingParam)
        val carsPage: Page<Car> = carRepository.findByStatus(Status.AVAILABLE, pageRequest)
        val cars = carsPage.content.map { it.toResponse() }
        return CarsListResponse(cars = cars)
    }
}