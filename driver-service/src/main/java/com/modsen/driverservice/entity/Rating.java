package com.modsen.driverservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "ratings")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    Driver driver;
    Integer score;
    Long passengerId;
}
