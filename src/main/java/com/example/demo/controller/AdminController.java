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

    @Autowired
    private FabricsRepository fabricsRepository;

    @GetMapping("api/admin/factory-list")
    public Map<String, Object> factory_market_list() {
        Map<String, Object> response = new HashMap<>();
        Iterable<Fabrics> fabrics = fabricsRepository.findAll();
        response.put("fabrics", fabrics);
        return response;
    }

    @PostMapping("/api/admin/add-factory")
    public Map<String, Object> addFabric(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        Fabrics fabric = new Fabrics(
                Double.parseDouble(body.get("new_price")),
                body.get("new_name"),
                Double.parseDouble(body.get("new_upgrad")),
                Double.parseDouble(body.get("new_mining")),
                body.get("image"));
        fabricsRepository.save(fabric);
        response.put("message", "OK");
        return response;
    }
}
