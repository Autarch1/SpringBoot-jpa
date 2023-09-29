package com.ypacode.springbootstudent.service;

import com.ypacode.springbootstudent.model.Course;
import com.ypacode.springbootstudent.repository.CourseRepo;
import com.ypacode.springbootstudent.repository.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CourseService {
    @Autowired
    private CourseRepo courseRepo;
    @Autowired
    private StudentRepo studentRepo;
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

    public Map<String, Long> getStudentCountPerCourse() {
        List<Course> courses = courseRepo.findAll();
        Map<String, Long> studentCountMap = new HashMap<>();

        for (Course course : courses) {
            long studentCount = course.getStudents().size();
            studentCountMap.put(course.getCourseName(), studentCount);
        }

        return studentCountMap;
    }
    public List<String> getMostPopularCourses(int topN) {
        List<Course> courses = courseRepo.findAll();

        // Sort courses by the number of students attending in descending order
        courses.sort((c1, c2) -> Integer.compare(c2.getStudents().size(), c1.getStudents().size()));

        // Get the top N courses
        List<String> mostPopularCourses = new ArrayList<>();
        for (int i = 0; i < topN && i < courses.size(); i++) {
            mostPopularCourses.add(courses.get(i).getCourseName());
        }

        return mostPopularCourses;
    }


}
