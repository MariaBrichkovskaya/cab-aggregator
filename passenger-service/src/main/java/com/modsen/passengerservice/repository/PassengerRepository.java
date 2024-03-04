package com.modsen.passengerservice.repository;

import com.modsen.passengerservice.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface PassengerRepository extends JpaRepository<Passenger, UUID> {
    boolean existsByEmailAndActiveIsTrue(String email);

    boolean existsByPhoneAndActiveIsTrue(String phone);

    Optional<Passenger> findByIdAndActiveIsTrue(UUID id);
}
