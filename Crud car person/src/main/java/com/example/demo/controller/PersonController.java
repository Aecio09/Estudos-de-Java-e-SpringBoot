package com.example.demo.controller;


import com.example.demo.controller.dto.CarResponse;
import com.example.demo.controller.dto.PersonCreate;
import com.example.demo.controller.dto.PersonResponse;
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
public class PersonController {

    public CarRepository carRepo;
    public PersonRepository personRepo;

    public PersonController(CarRepository carRepo, PersonRepository personRepo) {
        this.carRepo = carRepo;
        this.personRepo = personRepo;
    }

    @PostMapping("persons/")
    @Transactional
    public ResponseEntity<Void> personCreate(@RequestBody PersonCreate dto){
        Person person = new Person();
        person.setPersonName(dto.personName());

        try {
            personRepo.save(person);
            return ResponseEntity.status(200).build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PutMapping("persons/{id}")
    @Transactional
    public ResponseEntity<Void> personUpdate(@RequestBody PersonCreate dto, @PathVariable long id) {
        return personRepo.findById(id).map(person -> {
            person.setPersonName(dto.personName());
            try {
                personRepo.save(person);
                return ResponseEntity.status(200).<Void>build();
            } catch (DataIntegrityViolationException e) {
                return ResponseEntity.status(409).<Void>build();
            }
        }).orElse(ResponseEntity.status(404).build());
    }
    @GetMapping("persons/")
    public ResponseEntity<List<PersonResponse>> personList(){
        List<Person> person = personRepo.findAll();

        List<PersonResponse> personResponses = person.stream()
                .map(person1 -> new PersonResponse(
                        person1.getPersonName(),
                        person1.getOwnedCars().stream()
                                .map(car -> new CarResponse(car.getCarName(), car.getCarPlate(), null))
                                .toList() // uma stream dentro de outra stream, a que ponto chegamos.
                ))
                .toList();
        return ResponseEntity.status(200).body(personResponses);
    }
    @GetMapping("persons/{id}")
    public ResponseEntity<PersonResponse> personGet(@PathVariable long id){
        return personRepo.findById(id).map(person1 -> {
            PersonResponse personResponse = new PersonResponse(
                    person1.getPersonName(),
                    person1.getOwnedCars().stream()
                            .map(car -> new CarResponse(car.getCarName(), car.getCarPlate(), null))
                            .toList()
            );
            return ResponseEntity.status(200).body(personResponse);
        }).orElse(ResponseEntity.status(404).build());
    }

    @DeleteMapping("persons/{id}")
    @Transactional
    public ResponseEntity<Void> personDelete(@PathVariable long id) {
        Person person = personRepo.findById(id).orElseThrow();

        personRepo.deleteById(id);
        return ResponseEntity.status(200).build();
    }
}
