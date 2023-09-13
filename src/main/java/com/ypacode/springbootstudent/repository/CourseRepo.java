package com.ypacode.springbootstudent.repository;

import com.ypacode.springbootstudent.model.Course;
import com.ypacode.springbootstudent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface CourseRepo extends JpaRepository<Course, String> {

    @Query("SELECT MAX(c.courseId) FROM Course c")
    String findHighestCourseId();

    Optional<Course> findByCourseName(String courseName);

    Course findByCourseId(String courseId);

}
