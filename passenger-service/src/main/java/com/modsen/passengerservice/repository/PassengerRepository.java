package com.modsen.passengerservice.repository;

import com.modsen.passengerservice.entity.Passenger;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PassengerRepository extends JpaRepository<Passenger,Long> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
