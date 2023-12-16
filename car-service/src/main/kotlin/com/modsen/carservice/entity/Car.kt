package com.modsen.carservice.entity

import com.modsen.carservice.enum.Status
import jakarta.persistence.*

@Entity
@Table(name = "cars")
data class Car(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        var id: Long = 0L,
        @Column(name = "name")
        var name: String = "",
        @Column(name = "number")
        var number: String = "",
        @Column(name = "licence")
        var licence: String = "",
        @Column(name = "year")
        var year: Int = 0,
        @Column(name = "model")
        var model: String = "",
        @Column(name = "status")
        @Enumerated(EnumType.STRING)
        var status: Status = Status.AVAILABLE
)
