package com.modsen.passengerservice.mapper;

import com.modsen.passengerservice.dto.request.*;
import com.modsen.passengerservice.dto.response.*;
import com.modsen.passengerservice.entity.*;
import com.modsen.passengerservice.service.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PassengerMapper {
    private final ModelMapper modelMapper;
    private final RatingService ratingService;

    public PassengerResponse toPassengerResponse(Passenger passenger) {
        PassengerResponse response = modelMapper.map(passenger, PassengerResponse.class);
        response.setRating(ratingService.getAveragePassengerRating(passenger.getId()).getAverageRating());
        return response;
    }

    public Passenger toEntity(PassengerRequest request) {
        return modelMapper.map(request, Passenger.class);
    }

    public List<PassengerResponse> toPassengerResponseList(Page<Passenger> passengersPage) {
        return passengersPage.getContent().stream()
                .map(this::toPassengerResponse).toList();
    }

}
