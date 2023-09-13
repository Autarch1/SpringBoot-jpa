package com.ypacode.springbootstudent.dto;

import com.ypacode.springbootstudent.model.Student;

public class StudentDto {
    private String studentId;
    private String studentName;
    private String studentDob;
    private String studentGender;
    private String studentPhone;
    private String studentEducation;

    private String[] studentAttend;

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

    public String[] getStudentAttend() {
        return studentAttend;
    }

    public void setStudentAttend(String[] studentAttend) {
        this.studentAttend = studentAttend;
    }



    public Student convertToStudent(){
        return new Student(studentId,studentName,studentDob,studentGender,studentPhone,studentEducation,null,studentAttend,null);
    }
}
