package com.modsen.driverservice.entity;

import com.modsen.driverservice.enums.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "drivers")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "name",nullable = false)
    String name;
    @Column(name = "surname",nullable = false)
    String surname;
    @Column(name = "phone",unique = true,nullable = false)
    String phone;

    @Column(name = "rating")
    Double rating = 5.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    Status status = Status.AVAILABLE;

}
