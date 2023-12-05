package com.modsen.driverservice.entity;

import com.modsen.driverservice.entity.enums.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "drivers")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "name")
    String name;
    @Column(name = "surname")
    String surname;
    @Column(name = "phone")
    String phone;

    @Column(precision = 3, scale = 2, columnDefinition = "numeric")
    Double rating = 5.0;

    @Enumerated(EnumType.STRING)
    Status status = Status.AVAILABLE;

}
