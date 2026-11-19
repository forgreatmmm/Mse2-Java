package com.example.studentprofile.repository;

import java.util.List;
import com.example.studentprofile.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    List<Student> findByEmailIgnoreCase(String email);

    List<Student> findByCourseNameIgnoreCase(String courseName);
}
