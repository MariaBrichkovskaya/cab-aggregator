package com.modsen.passengerservice.repository;

import com.modsen.passengerservice.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> getRatingsByPassengerId(UUID passengerId);
}
