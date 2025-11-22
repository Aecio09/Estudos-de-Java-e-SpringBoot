package com.projeto.teste.demo.service;

import com.projeto.teste.demo.model.*;
import com.projeto.teste.demo.repository.*;
import org.springframework.stereotype.Service;


import java.util.List;
@Service
public class QuestionService {
	private final QuestionRepository repository;
	
	public QuestionService(QuestionRepository repository) {
		this.repository = repository;
	}
	
	public List<Question> list() {
		return repository.findAll();
	}
	public Question searchById(Long id) {
		return repository.findById(id).orElse(null);
	}
	public Question save(Question question) {
		return repository.save(question);
	}
	public Question update(Long id, Question newQuestion) {
		Question question = repository.findById(id).orElse(null);
		if (question == null) return null;
		
			question.setQuestionType(newQuestion.getQuestionType());
			question.setAnswerTrue(newQuestion.getAnswerTrue());
			question.setAnswerFalse1(newQuestion.getAnswerFalse1());
			question.setAnswerFalse2(newQuestion.getAnswerFalse2());
			question.setAnswerFalse3(newQuestion.getAnswerFalse3());
			
			return repository.save(question);
	}
	public void delete(Long id) {
		repository.deleteById(id);
	}
}
