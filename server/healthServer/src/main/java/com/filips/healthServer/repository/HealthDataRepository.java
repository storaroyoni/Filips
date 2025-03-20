package com.filips.healthServer.repository;

import com.filips.healthServer.model.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthDataRepository extends JpaRepository<HealthData, Integer> {
    List<HealthData> findByUserId(int userId);
}