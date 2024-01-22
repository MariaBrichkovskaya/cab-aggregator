package com.modsen.driverservice.repository;

import com.modsen.driverservice.entity.Driver;
import com.modsen.driverservice.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByPhone(String phone);

    Page<Driver> findByStatusAndActiveIsTrue(Status status, PageRequest pageRequest);

    Optional<Driver> findByIdAndActiveIsTrue(long id);
}
