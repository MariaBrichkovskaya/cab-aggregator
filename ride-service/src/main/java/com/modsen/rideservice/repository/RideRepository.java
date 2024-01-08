package com.modsen.rideservice.repository;

import com.modsen.rideservice.entity.Ride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends JpaRepository<Ride,Long> {
    Page<Ride> findAllByPassengerId(long passengerId, PageRequest pageRequest);

    Page<Ride> findAllByDriverId(long driverId, PageRequest pageRequest);

}
