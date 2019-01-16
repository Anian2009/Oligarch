package com.example.demo.controller;

import com.example.demo.domain.Fabrics;
import com.example.demo.repository.FabricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AdminController {

    private final FabricsRepository fabricsRepository;

    @Autowired
    public AdminController(FabricsRepository fabricsRepository) {
        this.fabricsRepository = fabricsRepository;
    }

    @GetMapping("api/admin/factory-list")
    public Map<String, Object> factoryMarketList() {
        Map<String, Object> response = new HashMap<>();
        Iterable<Fabrics> fabrics = fabricsRepository.findAll();
        response.put("fabrics", fabrics);
        return response;
    }

    @PostMapping("api/admin/add-factory")
    public Map<String, Object> addFabric(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        Fabrics fabric = new Fabrics(
                Double.parseDouble(body.get("newPrice")),
                body.get("newName"),
                Double.parseDouble(body.get("newUpgrade")),
                Double.parseDouble(body.get("newMining")),
                body.get("image"));
        fabricsRepository.save(fabric);
        response.put("message", "OK");
        return response;
    }
}
