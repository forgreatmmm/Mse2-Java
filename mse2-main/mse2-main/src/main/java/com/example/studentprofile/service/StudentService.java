package com.example.studentprofile.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.studentprofile.model.Student;
import com.example.studentprofile.repository.StudentRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StudentService {
    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public Student create(Integer id, String name, String email, String course,
                          Double programmingScore, Double mathematicsScore, List<String> skills) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student ID is required.");
        }
        validateScore(programmingScore, "Programming score");
        validateScore(mathematicsScore, "Mathematics score");
        validateSkills(skills);
        if (repository.existsById(id)) {
            throw duplicateId(id);
        }

        Student student = new Student(id, name, email, course, programmingScore, mathematicsScore, skills);
        try {
            return repository.saveAndFlush(student);
        } catch (DataIntegrityViolationException exception) {
            // The primary key also prevents duplicates from simultaneous requests.
            if (repository.existsById(id)) {
                throw duplicateId(id);
            }
            throw exception;
        }
    }

    public List<Student> findAll() {
        return repository.findAll();
    }

    public Student findById(int id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Student " + id + " was not found."));
    }

    @Transactional
    public Student update(int id, Double programmingScore, Double mathematicsScore, List<String> skills) {
        Student student = findById(id);
        validateScore(programmingScore, "Programming score");
        validateScore(mathematicsScore, "Mathematics score");
        validateSkills(skills);
        student.setProgrammingScore(programmingScore);
        student.setMathematicsScore(mathematicsScore);
        student.setTechnicalSkills(skills);
        // JPA saves changes to this managed student when the transaction commits.
        return student;
    }

    @Transactional
    public void delete(int id) {
        repository.delete(findById(id));
    }

    public List<Student> searchByEmail(String email) {
        return repository.findByEmailIgnoreCase(email);
    }

    public List<Student> searchByCourse(String course) {
        return repository.findByCourseNameIgnoreCase(course);
    }

    public List<Student> meritList() {
        List<Student> students = new ArrayList<>(repository.findAll());
        Comparator<Student> byMerit = Comparator.comparingDouble(Student::getAverageScore)
                .reversed()
                .thenComparingInt(Student::getStudentId);
        Collections.sort(students, byMerit);
        return students;
    }

    public Map<String, List<Student>> sharedSkills() {
        HashMap<String, List<Student>> groups = new HashMap<>();
        for (Student student : repository.findAll()) {
            for (String skill : student.getTechnicalSkills()) {
                groups.putIfAbsent(skill, new ArrayList<>());
                List<Student> group = groups.get(skill);
                if (!group.contains(student)) {
                    group.add(student);
                }
            }
        }
        groups.values().removeIf(group -> group.size() < 2);
        return groups;
    }

    public List<Student> needsImprovement() {
        List<Student> matches = new ArrayList<>();
        for (Student student : repository.findAll()) {
            if (student.getProgrammingScore() < 50 && student.getMathematicsScore() >= 75) {
                matches.add(student);
            }
        }
        return matches;
    }

    private void validateScore(Double score, String label) {
        if (score == null || !Double.isFinite(score) || score < 0 || score > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, label + " must be between 0 and 100.");
        }
    }

    private void validateSkills(List<String> skills) {
        if (skills == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Technical skills must be a list.");
        }
        for (String skill : skills) {
            if (skill == null || skill.isBlank() || skill.length() > 255) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Each technical skill must contain between 1 and 255 characters.");
            }
        }
    }

    private ResponseStatusException duplicateId(int id) {
        return new ResponseStatusException(HttpStatus.CONFLICT, "Student ID " + id + " already exists.");
    }
}
