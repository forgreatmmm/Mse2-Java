package com.example.studentprofile.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

import com.example.studentprofile.model.Student;
import com.example.studentprofile.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Student> create(@RequestBody StudentRequest request) {
        Student student = service.create(request.studentId(), request.studentName(), request.email(),
                request.courseName(), request.programmingScore(), request.mathematicsScore(), request.technicalSkills());
        return ResponseEntity.created(URI.create("/api/students/" + student.getStudentId())).body(student);
    }

    @GetMapping
    public List<Student> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Student findById(@PathVariable int id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable int id, @RequestBody StudentUpdateRequest request) {
        return service.update(id, request.programmingScore(), request.mathematicsScore(), request.technicalSkills());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        service.delete(id);
    }

    @GetMapping("/merit-list")
    public List<Student> meritList() {
        return service.meritList();
    }

    @GetMapping("/search/email")
    public List<Student> searchByEmail(@RequestParam String email) {
        return service.searchByEmail(email);
    }

    @GetMapping("/search/course")
    public List<Student> searchByCourse(@RequestParam String course) {
        return service.searchByCourse(course);
    }

    @GetMapping("/shared-skills")
    public Map<String, List<Student>> sharedSkills() {
        return service.sharedSkills();
    }

    @GetMapping("/needs-improvement")
    public List<Student> needsImprovement() {
        return service.needsImprovement();
    }
}
