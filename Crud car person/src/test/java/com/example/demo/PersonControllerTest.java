package com.example.demo;

import com.example.demo.controller.PersonController;
import com.example.demo.controller.dto.PersonCreate;
import com.example.demo.entities.Person;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.PersonRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // habilita o Mockito sem subir o contexto Spring
class PersonControllerTest {

    @Mock
    private PersonRepository personRepo; // mock do repositório — sem banco real

    @Mock
    private CarRepository carRepo;

    @InjectMocks
    private PersonController personController; // injeta os mocks no controller

    @Test
    @DisplayName("Deve criar uma pessoa e retornar status 200")
    void deveCriarPessoa() {
        // DADO: um DTO com o nome da pessoa
        PersonCreate dto = new PersonCreate("João Silva");

        // QUANDO: o repositório for chamado, simula o retorno da entidade salva
        Person personSalva = new Person();
        personSalva.setPersonName("João Silva");
        when(personRepo.save(any(Person.class))).thenReturn(personSalva);

        // ENTÃO: chama o método do controller diretamente e verifica o status
        ResponseEntity<Void> response = personController.personCreate(dto);

        assertThat(response.getStatusCode().value()).isEqualTo(200);

        // VERIFICA: que o save foi chamado exatamente 1 vez
        verify(personRepo, times(1)).save(any(Person.class));
    }
}
