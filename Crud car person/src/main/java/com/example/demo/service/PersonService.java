package com.example.demo.service;


import com.example.demo.controller.dto.PersonCreate;
import com.example.demo.entities.Person;
import com.example.demo.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public void create(PersonCreate dto) {
        Person person = new Person();
        person.setPersonName(dto.personName());
        personRepository.save(person);
    }

    public void update(long id, PersonCreate dto){
        Person person = personRepository.findById(id).orElseThrow(() -> new RuntimeException("user not found"));
        person.setPersonName(dto.personName());
        personRepository.save(person);
    }

    public List<Person> listPerson(){
        return personRepository.findAll();
    }

    public Person get(long id){
        return personRepository.findById(id).orElseThrow(() -> new RuntimeException("user not found"));
    }

    public void delete(long id){
        personRepository.findById(id).orElseThrow(() -> new RuntimeException("user not found"));
        personRepository.deleteById(id);
    }
}
