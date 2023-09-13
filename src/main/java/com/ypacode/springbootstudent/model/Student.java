package com.ypacode.springbootstudent.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name="student")
public class Student {
    @Id
    private String studentId;
    private String studentName;
    private String studentDob;
    private String studentGender;
    private String studentPhone;
    private String studentEducation;



    @ManyToMany
    @JoinTable(name = "stud_course", joinColumns =
    @JoinColumn(name ="student_id"),
            inverseJoinColumns = @JoinColumn(name="course_id")
    )
    private List<Course> courses;

    @Transient
    private String[] StudentAttend;
    @Lob
    private byte [] studentPhoto;



    public Student() {
    }

    public Student(String studentId, String studentName, String studentDob, String studentGender, String studentPhone, String studentEducation, List<Course> courses, String[] studentAttend, byte[] studentPhoto) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentDob = studentDob;
        this.studentGender = studentGender;
        this.studentPhone = studentPhone;
        this.studentEducation = studentEducation;
        this.courses = courses;
        StudentAttend = studentAttend;
        this.studentPhoto = studentPhoto;

    }

    public String[] getStudentAttend() {
        return StudentAttend;
    }

    public void setStudentAttend(String[] studentAttend) {
        StudentAttend = studentAttend;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentDob() {
        return studentDob;
    }

    public void setStudentDob(String studentDob) {
        this.studentDob = studentDob;
    }

    public String getStudentGender() {
        return studentGender;
    }

    public void setStudentGender(String studentGender) {
        this.studentGender = studentGender;
    }

    public String getStudentPhone() {
        return studentPhone;
    }

    public void setStudentPhone(String studentPhone) {
        this.studentPhone = studentPhone;
    }

    public String getStudentEducation() {
        return studentEducation;
    }

    public void setStudentEducation(String studentEducation) {
        this.studentEducation = studentEducation;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }



    public byte[] getStudentPhoto() {
        return studentPhoto;
    }

    public void setStudentPhoto(byte[] studentPhoto) {
        this.studentPhoto = studentPhoto;
    }





    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", studentDob='" + studentDob + '\'' +
                ", studentGender='" + studentGender + '\'' +
                ", studentPhone='" + studentPhone + '\'' +
                ", studentEducation='" + studentEducation + '\'' +
                ", courses=" + courses +
                ", studentPhoto=" + Arrays.toString(studentPhoto) +
                '}';
    }
}
