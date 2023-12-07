package com.modsen.driverservice.repository;

import com.modsen.driverservice.entity.Driver;
import com.modsen.driverservice.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver,Long> {
    boolean existsByPhone(String phone);

    List<Driver> findByStatus(Status status);
}
