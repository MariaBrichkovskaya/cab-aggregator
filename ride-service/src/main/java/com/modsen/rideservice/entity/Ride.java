package com.modsen.rideservice.entity;

import com.modsen.rideservice.enums.PaymentMethod;
import com.modsen.rideservice.enums.RideStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rides")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
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
    UUID passengerId;
    @Column(name = "driver_id")
    UUID driverId;
    LocalDateTime date;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    RideStatus rideStatus = RideStatus.CREATED;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    PaymentMethod paymentMethod;

}
