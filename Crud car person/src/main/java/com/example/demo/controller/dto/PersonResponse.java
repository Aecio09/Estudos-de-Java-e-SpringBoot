package com.example.demo.controller.dto;

import java.util.List;

public record PersonResponse(String personName, List<CarResponse> ownedCars) {
}
