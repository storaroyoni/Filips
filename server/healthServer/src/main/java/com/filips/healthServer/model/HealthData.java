package com.filips.healthServer.model;

import jakarta.persistence.*;
import lombok.CustomLog;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class HealthData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "health_data_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    @Column(name = "data_type")
    @Enumerated(value = EnumType.STRING)
    private DataTypes dataType;

    @Column(name = "value_numeric", nullable = false)
    private int valueNumeric;

    @Column(name = "value_text")
    private String valueText;

    private String unit;
}