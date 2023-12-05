package com.modsen.rideservice.entity;

import com.modsen.rideservice.enums.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "routes")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "pick_up_address")
    String pickUpAddress;
    @Column(name = "destination_address")
    String destinationAddress;
    @Column(name = "price")
    Double price;
    @Column(name = "passenger_id")
    Long passengerId;
    @Column(name = "driver_id")
    Long driverId;

    @Enumerated(EnumType.STRING)
    private Status status;

}
