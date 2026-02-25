package com.example.demo.controller;

import com.example.demo.controller.dto.CarCreate;
import com.example.demo.controller.dto.CarResponse;
import com.example.demo.entities.Car;
import com.example.demo.entities.Person;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CarController {

    public CarRepository carRepo;
    public PersonRepository personRepo;

    public CarController(CarRepository carRepo, PersonRepository personRepo) {
        this.carRepo = carRepo;
        this.personRepo = personRepo;
    }

    @PostMapping("car/")
    @Transactional
    public ResponseEntity<Void> createCar(@RequestBody CarCreate dto) {

        Car car = new Car();
        car.setCarName(dto.carName());
        car.setCarPlate(dto.carPlate());
        Person owner = dto.ownerId() != null ? personRepo.findById(dto.ownerId()).orElse(null)
                : null;
        car.setOwner(owner);

        try {
            carRepo.save(car);
            return ResponseEntity.status(201).build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        }
    }
    @PutMapping("car/{id}")
    @Transactional
    public ResponseEntity<Void> updateCar(@PathVariable("id") long id  , @RequestBody CarCreate dto){
    Car car = carRepo.findById(id);
    car.setCarName(dto.carName());
    car.setCarPlate(dto.carPlate());
    Person owner = dto.ownerId() != null ? personRepo.findById(dto.ownerId()).orElse(null) : null;
    car.setOwner(owner);

    try {
        carRepo.save(car);
        return  ResponseEntity.status(200).build();
    } catch (DataIntegrityViolationException e) {
        return  ResponseEntity.status(409).build();
    }

    }

    @GetMapping("car/")
    public ResponseEntity<List<CarResponse>> listCars(){
        List<Car> cars = carRepo.findAll();

        List<CarResponse> carResponses = cars.stream()
                .map(car -> new CarResponse(
                        car.getCarName(),
                        car.getCarPlate(),
                        car.getOwner() != null ? car.getOwner().getPersonName() : null
                ))
                .toList();
        return ResponseEntity.ok().body(carResponses);
    }

    @DeleteMapping("car/{id}")
    @Transactional
    public ResponseEntity<Void> deleteCar(@PathVariable Long id){

        Optional<Car> car = carRepo.findById(id);

        carRepo.deleteById(id);
        return ResponseEntity.status(200).build();
    }
}
