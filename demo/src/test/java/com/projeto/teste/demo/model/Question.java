package com.projeto.teste.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Question {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String questionType;
	
	private  String answerTrue;
	
	private String answerFalse1;
	
	private String answerFalse2;
	
	private String answerFalse3;
	
	public Question() {};
	
	public Question(String questionType, String answerTrue, String answerFalse1, String answerFalse2, String answerFalse3) {
		this.questionType = questionType;
		this.answerTrue = answerTrue;
		this.answerFalse1 = answerFalse1;
		this.answerFalse2 = answerFalse2;
		this.answerFalse3 = answerFalse3;
	}
	public long getId() {
		return id;
	}
	public String getQuestionType() {
		return questionType;
	}
	public String getAnswerTrue() {
		return answerTrue;
	}
	public String getAnswerFalse1() {
		return answerFalse1;
	}
	public String getAnswerFalse2() {
		return answerFalse2;
	}
	public String getAnswerFalse3() {
		return answerFalse3;
	}
	public void setAnswerFalse1(String answerFalse1) {
		this.answerFalse1 = answerFalse1;
	}
	public void setAnswerFalse2(String answerFalse2) {
		this.answerFalse2 = answerFalse2;
	}
	public void setAnswerFalse3(String answerFalse3) {
		this.answerFalse3 = answerFalse3;
	}
	public void setAnswerTrue(String answerTrue) {
		this.answerTrue = answerTrue;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}
																												
}
