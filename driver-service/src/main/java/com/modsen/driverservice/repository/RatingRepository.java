package com.modsen.driverservice.repository;

import com.modsen.driverservice.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> getRatingsByDriverId(UUID driverId);
}
