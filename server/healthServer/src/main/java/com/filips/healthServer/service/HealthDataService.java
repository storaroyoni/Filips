package com.filips.healthServer.service;

import com.filips.healthServer.model.HealthData;
import com.filips.healthServer.repository.HealthDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthDataService {

    @Autowired
    private HealthDataRepository healthDataRepository;

    @Async
    public HealthData saveHealthData(HealthData healthData) {
        return healthDataRepository.save(healthData);
    }

    public List<HealthData> getHealthDataByUserId(Long userId) {
        return healthDataRepository.findByUserId(userId);
    }
}