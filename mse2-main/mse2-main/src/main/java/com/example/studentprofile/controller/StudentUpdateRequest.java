package com.example.studentprofile.controller;

import java.util.List;

public record StudentUpdateRequest(Double programmingScore, Double mathematicsScore,
                                   List<String> technicalSkills) {
}
