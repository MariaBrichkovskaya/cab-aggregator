package com.modsen.rideservice.entity;

import com.modsen.rideservice.enums.PaymentMethod;
import com.modsen.rideservice.enums.RideStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@Builder
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "pick_up_address", nullable = false)
    String pickUpAddress;
    @Column(name = "destination_address", nullable = false)
    String destinationAddress;
    @Column(name = "price", nullable = false)
    Double price;
    @Column(name = "passenger_id", nullable = false)
    Long passengerId;
    @Column(name = "driver_id")
    Long driverId;
    LocalDateTime date;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    RideStatus rideStatus = RideStatus.CREATED;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    PaymentMethod paymentMethod;

}
