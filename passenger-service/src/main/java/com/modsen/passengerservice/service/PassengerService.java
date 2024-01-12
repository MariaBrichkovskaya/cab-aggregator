package com.modsen.passengerservice.service;

import com.modsen.passengerservice.dto.request.*;
import com.modsen.passengerservice.dto.response.*;

public interface PassengerService {
    PassengerResponse add(PassengerRequest request);

    PassengerResponse findById(Long id);

    PassengersListResponse findAll(int page, int size, String sortingParam);

    PassengerResponse update(PassengerRequest request, Long id);

    MessageResponse delete(Long id);
}
