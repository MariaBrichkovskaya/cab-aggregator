package com.modsen.carservice.exception

import com.modsen.carservice.util.Messages.CAR_ALREADY_EXISTS_MESSAGE

class AlreadyExistsException(val errors: Map<String, String>) : RuntimeException(CAR_ALREADY_EXISTS_MESSAGE)
