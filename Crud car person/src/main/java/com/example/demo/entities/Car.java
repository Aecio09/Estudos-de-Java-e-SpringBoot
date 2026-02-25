package com.example.demo.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long  id;

    @Column(nullable = false)
    private  String carName;

    @Column(nullable = false, unique = true)
    private String carPlate;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = true)
    private Person owner;
}
