package com.filips.healthServer.repository;

import com.filips.healthServer.model.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthDataRepository extends JpaRepository<HealthData, Integer> {
}