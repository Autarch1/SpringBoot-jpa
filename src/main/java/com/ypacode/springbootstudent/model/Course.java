package com.ypacode.springbootstudent.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity

public class Course {
    @Id
    private String courseId;
    @Column(unique = true)
    private String courseName;

    @ManyToMany(mappedBy = "courses",cascade = CascadeType.PERSIST)
    private List<Student> students;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
