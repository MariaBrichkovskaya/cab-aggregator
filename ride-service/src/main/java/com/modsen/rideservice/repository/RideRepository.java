package com.modsen.rideservice.repository;

import com.modsen.rideservice.entity.Ride;
import com.modsen.rideservice.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride,Long> {
    Page<Ride> findAllByPassengerIdAndStatus(long passengerId, Status status, PageRequest pageRequest);

    Page<Ride> findAllByDriverIdAndStatus(long driverId, Status status, PageRequest pageRequest);

}
