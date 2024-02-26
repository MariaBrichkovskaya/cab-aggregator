package com.modsen.passengerservice.service;

import com.modsen.passengerservice.dto.request.PassengerRequest;
import com.modsen.passengerservice.dto.response.MessageResponse;
import com.modsen.passengerservice.dto.response.PassengerResponse;
import com.modsen.passengerservice.dto.response.PassengersListResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.UUID;


public interface PassengerService {
    PassengerResponse add(OAuth2User principal);

    PassengerResponse findById(UUID id);

    PassengersListResponse findAll(int page, int size, String sortingParam);

    PassengerResponse update(PassengerRequest request, UUID id);

    MessageResponse delete(UUID id);
}
