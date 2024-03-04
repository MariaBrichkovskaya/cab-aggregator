package com.modsen.rideservice.repository;

import com.modsen.rideservice.entity.Ride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    Page<Ride> findAllByPassengerId(UUID passengerId, PageRequest pageRequest);

    Page<Ride> findAllByDriverId(UUID driverId, PageRequest pageRequest);

}
