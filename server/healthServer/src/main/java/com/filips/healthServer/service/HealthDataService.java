package com.filips.healthServer.service;

import com.filips.healthServer.model.DeviceType;
import com.filips.healthServer.model.HealthData;
import com.filips.healthServer.repository.HealthDataRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class HealthDataService {
    private final HealthDataRepository healthDataRepository;
    private final String API_KEY;
    private final String API_URL = "https://api.openai.com/v1/completions";

    public Map<String, List<HealthData>> fetchHealthData() {
        Map<String, List<HealthData>> healthInfo = new HashMap<>(Map.of());
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

    public String analyzeData() {
        RestTemplate restTemplate = new RestTemplate();


        String instruction = "Please look at the datasets provided below. They consist of firstly data about the user's health condition and secondly data about the user's living space. Find correlations and analyze the data in about 4 to 5 sentences: ";
        int tokens = 80;

        Map<String, List<HealthData>> healthInfo = new java.util.HashMap<>(Map.of());
        healthInfo.put(DeviceType.SMARTWATCH.toString(), healthDataRepository.findRecentHealthData(DeviceType.SMARTWATCH.toString(), LocalDateTime.now().minusDays(7)));
        healthInfo.put(DeviceType.HEALTH_HUB.toString(), healthDataRepository.findRecentHealthData(DeviceType.HEALTH_HUB.toString(), LocalDateTime.now().minusDays(7)));

        instruction += healthInfo.toString();
        tokens += 80;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");
        requestBody.put("max_tokens", tokens);
        requestBody.put("messages", new Object[]{
                Map.of("role", "user", "content", instruction)
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(API_URL, HttpMethod.POST, request, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("choices")) {
            Map<String, Object> choice = ((Map<String, Object>) ((java.util.List<?>) response.getBody().get("choices")).get(0));
            Map<String, Object> message = (Map<String, Object>) choice.get("message");
            return (String) message.get("content");
        }

        return "No response from ChatGPT.";
    }
}
