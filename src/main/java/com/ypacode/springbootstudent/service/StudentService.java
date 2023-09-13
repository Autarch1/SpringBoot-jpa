package com.ypacode.springbootstudent.service;

import com.ypacode.springbootstudent.model.Student;
import com.ypacode.springbootstudent.model.User;
import com.ypacode.springbootstudent.repository.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepo studentRepo;

    public List<Student> getAllStudent(){
        return studentRepo.findAll();
    }

    public int save(Student student){
            student.setStudentId(generateNextStudentId());
            try{
                studentRepo.save(student);
                return 1;
            }catch (Exception e){
                e.printStackTrace();
                return 0;
            }
    }

    public String generateNextStudentId() {
        String highestStudentId = studentRepo.findHighestStudentId();

        if (highestStudentId == null) {
            return "STU001";
        } else {
            int numericPart = Integer.parseInt(highestStudentId.substring(3)) + 1;
            return String.format("STU%03d", numericPart);
        }
    }

    public boolean isPhoneInUse(String phone) {
        Optional<Student> existingPhone = studentRepo.findByStudentPhone(phone);
        return existingPhone.isPresent();
    }

    public void deleteStudent(String studentId) {
        studentRepo.deleteById(studentId);

    }
    public Student getStudentById(String studentId) {
        return studentRepo.findByStudentId(studentId);
    }

    public int updateStudent(Student student){
        try{
            studentRepo.save(student);
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
}
