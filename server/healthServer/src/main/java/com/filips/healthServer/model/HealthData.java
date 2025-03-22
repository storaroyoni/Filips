package com.filips.healthServer.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "health_data")
public class HealthData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "health_data_id")
    private int id;

    @Enumerated(value = EnumType.STRING)
    private DeviceType device;

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    @Column(name = "data_type")
    @Enumerated(value = EnumType.STRING)
    private DataTypes dataType;

    @Column(name = "value_numeric", nullable = false)
    private int valueNumeric;

    @Column(name = "is_public")
    private boolean isPublic;
}