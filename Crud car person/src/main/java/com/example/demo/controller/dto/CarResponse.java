package com.example.demo.controller.dto;

import com.example.demo.entities.Person;

public record CarResponse(
        String carName,
        String carPlate,
        String ownerUsername
) {
}
