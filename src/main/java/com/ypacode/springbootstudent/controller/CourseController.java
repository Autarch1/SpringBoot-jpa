package com.ypacode.springbootstudent.controller;

import com.ypacode.springbootstudent.model.Course;
import com.ypacode.springbootstudent.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping(value = "courseRegister")
    public ModelAndView  courseView(ModelMap model){
        Course courses = new Course();
        String nextCourseId = courseService.generateNextCourseId();
        courses.setCourseId(nextCourseId);
        return new ModelAndView("CourseRegister","courseBean", courses);
    }

    @PostMapping(value = "ProcessCourseRegister")
    public String courseAdd(@ModelAttribute("courseBean") @Validated Course cb, BindingResult br,ModelMap model){
        if(br.hasErrors()){
            System.out.println("brError");
            return "courseRegister";
        }

        if(cb.getCourseId().equals("") || cb.getCourseName().equals("")){
            model.addAttribute("blank", "Field Cannot be blank");
            return "courseRegister";
        }
        if(courseService.isCourseInUSer(cb.getCourseName().trim())){
            model.addAttribute("sameCourse", "This Course is already exist");
            return "courseRegister";
        }

        int i =courseService.saveCourse(cb);
        if(i == 0){
            model.addAttribute("insertError", "Course Register Failed");
            return "courseRegister";
        }

        return "redirect:/";
    }




    @GetMapping("/courseList")
    public String showCourseStatistics(ModelMap model) {
        int topN = 5; // Change this to the desired number of top courses to display
        List<String> mostPopularCourses = courseService.getMostPopularCourses(topN);
        Map<String, Long> studentCountMap = courseService.getStudentCountPerCourse();

        // Filter the student count map to include only the top N courses
        Map<String, Long> topStudentCountMap = mostPopularCourses.stream()
                .collect(Collectors.toMap(course -> course, studentCountMap::get));

        model.addAttribute("mostPopularCourses", mostPopularCourses);
        model.addAttribute("studentCountMap", topStudentCountMap);
        return "CourseList";
    }


}
