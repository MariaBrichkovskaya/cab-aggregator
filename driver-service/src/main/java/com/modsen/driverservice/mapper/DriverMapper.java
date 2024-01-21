package com.modsen.driverservice.mapper;

import com.modsen.driverservice.dto.request.DriverRequest;
import com.modsen.driverservice.dto.response.DriverResponse;
import com.modsen.driverservice.entity.Driver;
import com.modsen.driverservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DriverMapper {
    private final ModelMapper modelMapper;
    private final RatingService ratingService;

    public DriverResponse toDriverResponse(Driver driver) {
        DriverResponse response = modelMapper.map(driver, DriverResponse.class);
        response.setRating(ratingService.getAverageDriverRating(driver.getId()).getAverageRating());
        return response;
    }

    public Driver toEntity(DriverRequest request) {
        return modelMapper.map(request, Driver.class);
    }

    public List<DriverResponse> toDriverResponseList(Page<Driver> driversPage) {
        return driversPage.getContent().stream()
                .map(this::toDriverResponse).toList();
    }

}

