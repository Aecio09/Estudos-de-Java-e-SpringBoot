package com.example.demo.controller;


import com.example.demo.entities.Bread;
import com.example.demo.repository.BreadRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/bread")
public class BreadController {

    public final BreadRepository breadRepository;

    public BreadController(BreadRepository breadRepository) {
        this.breadRepository = breadRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addBread(@RequestBody String name) {
        try {
            Bread bread = new Bread();
            bread.setBreadName(name);
            breadRepository.save(bread);
            return ResponseEntity.ok("Bread added successfully" + URI.create("/bread/" + bread.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error adding bread: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Bread>> getAllBread() {
        try {
            List<Bread> breads = breadRepository.findAll();
            return ResponseEntity.ok(breads);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
