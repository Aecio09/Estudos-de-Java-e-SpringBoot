package com.example.demo.controller.dto;

import com.example.demo.entities.Person;

public record CarCreate(String carName, String carPlate, Long ownerId) {
}
