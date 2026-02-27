package com.example.demo.controller;


import com.example.demo.controller.dto.CarResponse;
import com.example.demo.controller.dto.PersonCreate;
import com.example.demo.controller.dto.PersonResponse;
import com.example.demo.service.PersonService;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping("persons/")
    @Transactional
    public ResponseEntity<Void> personCreate(@RequestBody PersonCreate dto) {
        try {
            personService.create(dto);
            return ResponseEntity.status(200).build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PutMapping("persons/{id}")
    @Transactional
    public ResponseEntity<Void> personUpdate(@RequestBody PersonCreate dto, @PathVariable long id) {
        try {
            personService.update(id, dto);
            return ResponseEntity.status(200).build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("persons/")
    public ResponseEntity<List<PersonResponse>> personList() {
        List<PersonResponse> response = personService.listPerson().stream()
                .map(p -> new PersonResponse(
                        p.getPersonName(),
                        p.getOwnedCars().stream()
                                .map(car -> new CarResponse(car.getCarName(), car.getCarPlate(), null))
                                .toList()
                ))
                .toList();
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("persons/{id}")
    public ResponseEntity<PersonResponse> personGet(@PathVariable long id) {
        try {
            var p = personService.get(id);
            var response = new PersonResponse(
                    p.getPersonName(),
                    p.getOwnedCars().stream()
                            .map(car -> new CarResponse(car.getCarName(), car.getCarPlate(), null))
                            .toList()
            );
            return ResponseEntity.status(200).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("persons/{id}")
    @Transactional
    public ResponseEntity<Void> personDelete(@PathVariable long id) {
        try {
            personService.delete(id);
            return ResponseEntity.status(200).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }
}

