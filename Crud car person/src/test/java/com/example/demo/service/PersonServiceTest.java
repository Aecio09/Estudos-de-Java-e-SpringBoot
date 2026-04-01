package com.example.demo.service;

import com.example.demo.controller.dto.PersonCreate;
import com.example.demo.entities.Person;
import com.example.demo.repository.PersonRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // habilita o Mockito
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    @Test
    @DisplayName("Should CREATE a Person")
    void shouldCreateAPerson() {
        // ARRANGE
        PersonCreate dto = new PersonCreate("Maria");

        // ACT
        personService.create(dto);

        // ASSERT
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        verify(personRepository, times(1)).save(captor.capture());

        Person personSave = captor.getValue();
        assertThat(personSave.getPersonName()).isEqualTo("Maria");
    }

    @Test
    @DisplayName("Should UPDATE a Person")
    void shouldUpdateAPerson() {
        // ARRANGE
        long id = 1L;
        Person person = new Person();
        person.setPersonName("Roberto");

        when(personRepository.findById(id)).thenReturn(Optional.of(person));

        PersonCreate dto = new PersonCreate("Sergio");

        // ACT
        personService.update(id, dto);

        // ASSERT
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        verify(personRepository, times(1)).save(captor.capture());

        assertThat(captor.getValue().getPersonName()).isEqualTo("Sergio");
    }

    @Test
    @DisplayName("UPDATE Should throw an error when person not found")
    void updateShouldThrowAnErrorWhenPersonNotFound(){
        // ARRANGE
        when(personRepository.findById(99L)).thenReturn(Optional.empty());

        PersonCreate dto = new PersonCreate("Goku");

        // ACT e ASSERT
        assertThrows(RuntimeException.class, () -> personService.update(99L, dto));
    }

    @Test
    @DisplayName("Should list and return a list of person")
    void shouldListPersonAndReturnAListOfPerson() {
        // ARRANGE
        Person person1 = new Person();
        person1.setPersonName("Naruto");
        Person person2 = new Person();
        person2.setPersonName("Roberto Carlos");

        when(personRepository.findAll()).thenReturn(List.of(person1, person2));

        // ACT
        List<Person> persons = personService.listPerson();

        // ASSERT
        assertThat(persons).hasSize(2);
        assertThat(persons.get(0).getPersonName()).isEqualTo("Naruto");
        assertThat(persons.get(1).getPersonName()).isEqualTo("Roberto Carlos");
    }

    @Test
    @DisplayName("Should get a person and then return the person")
    void shouldGetAPersonAndThenReturnThePerson() {
        // ARRANGE
        Person person = new Person();
        long id = 1L;
        person.setPersonName("Varka");

        when(personRepository.findById(id)).thenReturn(Optional.of(person));

        // ACT
        Person getPerson = personService.get(id);

        // ASSERT
        assertThat(getPerson.getPersonName()).isEqualTo("Varka");
    }

    @Test
    void delete() {
    }
}