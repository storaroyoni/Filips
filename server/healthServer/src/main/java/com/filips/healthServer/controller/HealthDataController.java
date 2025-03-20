package com.filips.healthServer.controller;

import com.filips.healthServer.model.HealthData;
import com.filips.healthServer.service.HealthDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/health-data")
public class HealthDataController {

    @Autowired
    private HealthDataService healthDataService;

    @PostMapping
    public HealthData saveHealthData(@RequestBody HealthData healthData) {
        return healthDataService.saveHealthData(healthData);
    }

    @GetMapping("/{userId}")
    public List<HealthData> getHealthData(@PathVariable Long userId) {
        return healthDataService.getHealthDataByUserId(userId);
    }
}