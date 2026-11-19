package com.example.studentprofile.controller;

import java.util.List;

public record StudentRequest(Integer studentId, String studentName, String email, String courseName,
                             Double programmingScore, Double mathematicsScore, List<String> technicalSkills) {
}
