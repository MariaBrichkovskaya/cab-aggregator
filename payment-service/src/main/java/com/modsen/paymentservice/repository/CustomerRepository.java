package com.modsen.paymentservice.repository;

import com.modsen.paymentservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<User,Long> {
}
