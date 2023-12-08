package com.modsen.rideservice.entity;

import com.modsen.rideservice.enums.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "rides")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "pick_up_address",nullable = false)
    String pickUpAddress;
    @Column(name = "destination_address",nullable = false)
    String destinationAddress;
    @Column(name = "price",nullable = false)
    Double price;
    @Column(name = "passenger_id",nullable = false)
    Long passengerId;
    @Column(name = "driver_id",nullable = false)
    Long driverId;

    @Enumerated(EnumType.STRING)
    Status status=Status.CREATED;

}
