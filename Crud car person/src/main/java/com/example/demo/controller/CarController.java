package com.example.demo.controller;

import com.example.demo.controller.dto.CarCreate;
import com.example.demo.controller.dto.CarResponse;
import com.example.demo.service.CarService;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping("car/")
    @Transactional
    public ResponseEntity<Void> createCar(@RequestBody CarCreate dto) {
        try {
            carService.create(dto);
            return ResponseEntity.status(201).build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PutMapping("car/{id}")
    @Transactional
    public ResponseEntity<Void> updateCar(@PathVariable long id, @RequestBody CarCreate dto) {
        try {
            carService.update(id, dto);
            return ResponseEntity.status(200).build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("car/")
    public ResponseEntity<List<CarResponse>> listCars() {
        List<CarResponse> response = carService.listCar().stream()
                .map(car -> new CarResponse(
                        car.getCarName(),
                        car.getCarPlate(),
                        car.getOwner() != null ? car.getOwner().getPersonName() : null
                ))
                .toList();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("car/{id}")
    @Transactional
    public ResponseEntity<Void> deleteCar(@PathVariable long id) {
        try {
            carService.delete(id);
            return ResponseEntity.status(200).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }
}

