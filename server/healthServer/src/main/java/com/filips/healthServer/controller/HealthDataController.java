package com.filips.healthServer.controller;

import com.filips.healthServer.model.HealthData;
import com.filips.healthServer.service.HealthDataService;
import jakarta.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/health")
public class HealthDataController {

    private final HealthDataService healthDataService;

    @GetMapping
    public ResponseEntity<Map<String, List<HealthData>>> fetchHealthData(){
        return new ResponseEntity<>(healthDataService.fetchHealthData(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<List<HealthData>> uploadData(@RequestBody List<HealthData> hps){
        return new ResponseEntity<>(healthDataService.saveHealthData(hps), HttpStatus.CREATED);
    }

    @PutMapping("/{status}")
    public ResponseEntity<List<HealthData>> updateStatus( @PathVariable boolean status ){
        return new ResponseEntity<>(healthDataService.updateStatus(status), HttpStatus.OK);
    }

    @GetMapping("/public")
    public ResponseEntity<List<HealthData>> fetchPublicData(){
        return new ResponseEntity<>(healthDataService.fetchPublicData(), HttpStatus.FOUND);
    }
}
