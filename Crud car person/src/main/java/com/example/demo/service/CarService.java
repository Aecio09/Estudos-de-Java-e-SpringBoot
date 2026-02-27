package com.example.demo.service;

import com.example.demo.controller.dto.CarCreate;
import com.example.demo.entities.Car;
import com.example.demo.entities.Person;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final PersonRepository personRepository;

    public CarService(CarRepository carRepository, PersonRepository personRepository) {
        this.carRepository = carRepository;
        this.personRepository = personRepository;
    }

    public void create(CarCreate dto){
        Car car = new Car();
        car.setCarName(dto.carName());
        car.setCarPlate(dto.carPlate());
        Person owner = dto.ownerId() != null ? personRepository.findById(dto.ownerId()).orElse(null)
                : null;
        car.setOwner(owner);
        carRepository.save(car);
    }

    public void update(long id, CarCreate dto){
        Car car = carRepository.findById(id);
        if (car == null) throw new RuntimeException("car not found");
        car.setCarName(dto.carName());
        car.setCarPlate(dto.carPlate());
        Person owner = dto.ownerId() != null ? personRepository.findById(dto.ownerId()).orElse(null)
                : null;
        car.setOwner(owner);
        carRepository.save(car);
    }

    public List<Car> listCar(){
        return carRepository.findAll();
    }

    public Car get(long id){
        Car car = carRepository.findById(id);
        if (car == null) throw new RuntimeException("car not found");
        return car;
    }

    public void delete(long id){
        if (carRepository.findById(id) == null) throw new RuntimeException("car not found");
        carRepository.deleteById(id);
    }
}
