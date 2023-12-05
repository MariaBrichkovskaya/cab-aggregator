package com.modsen.passengerservice.service.impl;

import com.modsen.passengerservice.dto.request.PassengerRequest;
import com.modsen.passengerservice.dto.response.PassengerResponse;
import com.modsen.passengerservice.entity.Passenger;
import com.modsen.passengerservice.repository.PassengerRepository;
import com.modsen.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {
    private final ModelMapper modelMapper;
    private final PassengerRepository passengerRepository;

    private PassengerResponse toDto(Passenger passenger){
        return modelMapper.map(passenger,PassengerResponse.class);
    }

    private Passenger toEntity(PassengerRequest request){
        return modelMapper.map(request,Passenger.class);
    }

    @Override
    public void add(PassengerRequest request) {
        passengerRepository.save(toEntity(request));
    }

    @Override
    public PassengerResponse findById(Long id) {
        Passenger passenger=passengerRepository.findById(id).get();
        return toDto(passenger);
    }

    @Override
    public List<PassengerResponse> findAll() {
        return passengerRepository.findAll().stream()
                .map(this::toDto).toList();
    }

    @Override
    public void update(PassengerRequest request, Long id) {
        Passenger passenger = toEntity(request);
        passenger.setId(id);
        passengerRepository.save(passenger);
    }

    @Override
    public void delete(Long id) {
        passengerRepository.deleteById(id);
    }
}
