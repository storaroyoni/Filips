package com.filips.healthServer.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    private int deviceId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(name = "device_type")
    @Enumerated(value = EnumType.STRING)
    private DeviceType deviceType;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "last_sync_date")
    private LocalDateTime lastSyncDate;

    @OneToMany
    private List<HealthData> healthData;
}
