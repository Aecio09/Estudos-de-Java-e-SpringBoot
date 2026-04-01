package com.example.demo.controller;

import com.example.demo.controller.dto.PersonDTO;
import com.example.demo.entity.Person;
import com.example.demo.repository.PersonRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonRepository personRepository;

    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @PostMapping
    public ResponseEntity<Void> createPerson(@RequestBody PersonDTO personDTO) {
        Person person = new Person();
        person.setName(personDTO.name());
        personRepository.save(person);

        return ResponseEntity.created(URI.create("/person/" + person.getId())).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePerson(@PathVariable("id") UUID id, @RequestBody PersonDTO personDTO) {
        return personRepository.findById(id)
                .map(person -> {
                    person.setName(personDTO.name());
                    personRepository.save(person);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable("id") UUID id) {
        if (!personRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        personRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Person>> getAllPerson() {
        return ResponseEntity.ok(personRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable("id") UUID id) {
        return personRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
