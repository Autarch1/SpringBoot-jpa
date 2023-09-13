package com.ypacode.springbootstudent.service;

import com.ypacode.springbootstudent.model.Course;
import com.ypacode.springbootstudent.model.User;
import com.ypacode.springbootstudent.repository.CourseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    private CourseRepo courseRepo;

    public List<Course> getAllCourses(){
        return courseRepo.findAll();
    }

    public int saveCourse(Course courses){
        courses.setCourseId(generateNextCourseId());
        try
        {
         courseRepo.save(courses);
         return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public String generateNextCourseId() {
        String highestCourseId = courseRepo.findHighestCourseId();

        if (highestCourseId == null) {
            return "COU001";
        } else {
            int numericPart = Integer.parseInt(highestCourseId.substring(3)) + 1;
            return String.format("COU%03d", numericPart);
        }
    }

    public boolean isCourseInUSer(String courseName) {
        Optional<Course> existingCourse = courseRepo.findByCourseName(courseName);
        return existingCourse.isPresent();
    }

    public Course getCourseById(String courseId){
        return courseRepo.findByCourseId(courseId);
    }

}
