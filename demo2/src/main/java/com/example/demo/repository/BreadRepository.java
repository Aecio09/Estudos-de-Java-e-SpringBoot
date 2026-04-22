package com.example.demo.repository;

import com.example.demo.entities.Bread;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BreadRepository extends JpaRepository<Bread, Long> {
}
