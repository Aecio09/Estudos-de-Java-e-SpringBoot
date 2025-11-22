package com.projeto.teste.demo.controller;

import com.projeto.teste.demo.model.*;
import com.projeto.teste.demo.service.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")

public class QuestionController {
	private final QuestionService service;
	
	public QuestionController(QuestionService service) {
		this.service = service;
	}
	
	@GetMapping
	public List<Question> list() {
		return service.list();
	}
	@GetMapping("/{id}")
	public Question find(@PathVariable Long id) {
		return service.searchById(id);
	}
	@PostMapping 
	public Question create(@RequestBody Question question)  {
		return service.save(question);
	}
	@PutMapping("/{id}")
	public Question update(@PathVariable Long id, @RequestBody Question question) {
		return service.update(id, question);
	}
	@DeleteMapping("/{id}") 
	public void delete(@PathVariable Long id) {
		service.delete(id);
	}
}
