package com.ypacode.springbootstudent.repository;

import com.ypacode.springbootstudent.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudentRepo extends JpaRepository<Student, String> {
    @Query("SELECT MAX(s.studentId) FROM Student s")
    String findHighestStudentId();

    Optional<Student> findByStudentPhone(String phone);
    Student findByStudentId(String studentId);

}
