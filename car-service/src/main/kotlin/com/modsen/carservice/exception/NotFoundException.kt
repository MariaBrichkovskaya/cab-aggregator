package com.modsen.carservice.exception

import com.modsen.carservice.util.Messages.CAR_NOT_FOUND_MESSAGE


class NotFoundException(id: Long?) : RuntimeException(String.format(CAR_NOT_FOUND_MESSAGE, id))
