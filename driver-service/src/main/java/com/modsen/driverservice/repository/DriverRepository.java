package com.modsen.driverservice.repository;

import com.modsen.driverservice.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver,Long> {
    boolean existsByPhone(String phone);
}
