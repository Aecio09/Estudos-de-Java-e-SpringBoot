package com.projeto.teste.demo.repository;

import com.projeto.teste.demo.model.Question;
import org.springframework.data.jpa.repository.*;

public interface QuestionRepository extends JpaRepository<Question, Long> {
	
}
