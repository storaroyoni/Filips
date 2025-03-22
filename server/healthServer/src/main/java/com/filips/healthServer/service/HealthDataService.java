package com.filips.healthServer.service;

import com.filips.healthServer.model.DeviceType;
import com.filips.healthServer.model.HealthData;
import com.filips.healthServer.repository.HealthDataRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class HealthDataService {
    private final HealthDataRepository healthDataRepository;

    public Map<String, List<HealthData>> fetchHealthData() {
        Map<String, List<HealthData>> healthInfo = new java.util.HashMap<>(Map.of());
        healthInfo.put(DeviceType.SMARTWATCH.toString(), healthDataRepository.findRecentHealthData(DeviceType.SMARTWATCH.toString(), LocalDateTime.now().minusDays(7)));
        healthInfo.put(DeviceType.HEALTH_HUB.toString(), healthDataRepository.findRecentHealthData(DeviceType.HEALTH_HUB.toString(), LocalDateTime.now().minusDays(7)));
        return healthInfo;
    }


    public List<HealthData> saveHealthData(List<HealthData> hps) {
        return healthDataRepository.saveAll(hps);
    }

    public List<HealthData> updateStatus(boolean status) {
        List<HealthData> healthData = healthDataRepository.findAll();
        return healthDataRepository.saveAll(healthData.stream().peek(hd -> hd.setPublic(status)).collect(Collectors.toList()));
    }

    public List<HealthData> fetchPublicData() {
        return healthDataRepository.findPublicHealthData(true, LocalDateTime.now().minusDays(7));
    }
}
