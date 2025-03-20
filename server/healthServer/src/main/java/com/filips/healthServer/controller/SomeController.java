package com.filips.healthServer.controller;

import com.filips.healthServer.model.Mod;
import com.filips.healthServer.repository.RandRepo;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@RestController
public class SomeController {

    private final RandRepo randRepo;

    @GetMapping("rand")
    public List<String> random(){
        return randRepo.findAll().stream().map(Mod::getName).toList();
    }

    @PostMapping("rand")
    public Mod newRand(@RequestBody Mod mod){
        return randRepo.save(mod);
    }
}
