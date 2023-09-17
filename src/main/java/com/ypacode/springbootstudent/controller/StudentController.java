package com.ypacode.springbootstudent.controller;

import com.ypacode.springbootstudent.dto.StudentDto;
import com.ypacode.springbootstudent.model.Course;
import com.ypacode.springbootstudent.model.Student;
import com.ypacode.springbootstudent.service.CourseService;
import com.ypacode.springbootstudent.service.ReportService;
import com.ypacode.springbootstudent.service.StudentService;
import com.ypacode.springbootstudent.util.StudentExcelExporter;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
public class StudentController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private ReportService reportService;
    @GetMapping(value = "studentRegister")
    public ModelAndView studRegisterView(ModelMap model) {
        Student student = new Student();
        String nextStudentId = studentService.generateNextStudentId();
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        student.setStudentId(nextStudentId);
        return new ModelAndView("StudentRegister", "studentBean", student);
    }

    @PostMapping(value = "StudentRegisterProcess")
    public String process(@ModelAttribute("studentBean") @Validated StudentDto sdto, BindingResult br,
                          ModelMap model, @RequestParam("studentPhoto") MultipartFile photo) {

        List<Course> courses = courseService.getAllCourses();

        if (sdto.getStudentId().equals("") || sdto.getStudentName().equals("") || sdto.getStudentDob().equals("") ||
                sdto.getStudentPhone().equals("") || sdto.getStudentEducation().equals("")) {
            model.addAttribute("courses", courses);

            model.addAttribute("blank", "Field cannot be blank");
            return "studentRegister";
        }

        if (studentService.isPhoneInUse(sdto.getStudentPhone())) {
            model.addAttribute("courses", courses);
            model.addAttribute("dup", "This phone Number is already used by other student");
            return "studentRegister";
        }
        Student sb = sdto.convertToStudent();

        try {
            if (!photo.isEmpty()) {
                byte[] studentPhoto = photo.getBytes();
                sb.setStudentPhoto(studentPhoto);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String[] courseIds = sb.getStudentAttend();
        List<Course> selectedCourses = new ArrayList<>();

        for (String courId : courseIds) {
            selectedCourses.add(courseService.getCourseById(courId));
        }
        sb.setCourses(selectedCourses);

        System.out.println(Arrays.toString(sb.getStudentPhoto()));
        int i = studentService.save(sb);
        if (i == 0) {
            model.addAttribute("fail", "Registeer failed");
            model.addAttribute("courses", courses);
            return "studentRegister";
        }


        return "redirect:/";
    }

    @GetMapping("/studentList")
    public String studentView(ModelMap model) {
        List<Student> studList = studentService.getAllStudent();
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        model.addAttribute("studList", studList);

        System.out.println(studList);
        return "StudentList";
    }

    @GetMapping("/studentPhoto")
    public void displayPhoto(@RequestParam("studentId") String studentId, HttpServletResponse response) {
        Student student = studentService.getStudentById(studentId);
        System.out.println(studentId);
        if (student != null && student.getStudentPhoto() != null) {
            String contentType = "image/jpeg"; // Default to JPEG
            if (Arrays.equals(Arrays.copyOf(student.getStudentPhoto(), 8), new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A})) {
                contentType = "image/png";
            }
            response.setContentType(contentType);
            try (OutputStream outputStream = response.getOutputStream()) {
                byte[] photoData = student.getStudentPhoto();
                outputStream.write(photoData);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @GetMapping(value = "/studentUpdate/{studentId}")
    public ModelAndView studentUpdateView(@PathVariable String studentId, ModelMap m) {
        Student selectedStudent = studentService.getStudentById(studentId);
        List<Course> courses = courseService.getAllCourses();
        m.addAttribute("courses", courses);
        m.addAttribute("selectedStudent", selectedStudent);
        return new ModelAndView("StudentUpdate", "updateBean", selectedStudent);
    }

    @PostMapping(value = "/StudentUpdateProcess")
    public String updateProcess(@ModelAttribute("updateBean") StudentDto sdto, BindingResult br, ModelMap model,
                                @RequestParam("studentPhoto") MultipartFile photo) {


        if (sdto.getStudentId().equals("") || sdto.getStudentName().equals("") || sdto.getStudentDob().equals("") ||
                sdto.getStudentPhone().equals("") || sdto.getStudentEducation().equals("")) {

                model.addAttribute("blank", "Field cannot be blank");
            return "redirect:/studentUpdate/" + sdto.getStudentId();
        }


        Student sb = sdto.convertToStudent();
        Student existingStudent = studentService.getStudentById(sdto.getStudentId());




        try {
            if (!photo.isEmpty()) {
                byte[] studentPhoto = photo.getBytes();
                sb.setStudentPhoto(studentPhoto);
            } else if (existingStudent != null && existingStudent.getStudentPhoto() != null) {
                sb.setStudentPhoto(existingStudent.getStudentPhoto());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        String[] courseIds = sb.getStudentAttend();
        List<Course> selectedCourses = new ArrayList<>();

        for (String courId : courseIds) {
            selectedCourses.add(courseService.getCourseById(courId));
        }
        sb.setCourses(selectedCourses);

        int result = studentService.updateStudent(sb);
        if(result == 0){
            model.addAttribute("fail", "Update Failed");
            return "redirect:/studentUpdate/" + sdto.getStudentId();
        }

        return "redirect:/studentList";


    }
    @GetMapping(value = "/disableStudent/{studentId}")
    public String disableStudent(@PathVariable String studentId){
                studentService.deleteStudent(studentId);

            return "redirect:/studentList";

    }

    @GetMapping(value = "/jasper-pdf/export")
    public void createPDF(HttpServletResponse response) throws IOException, JRException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyy-MM-dd:hh:mm:ss");
        String currentDateTIme = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue ="attachment; filename=pdf_" + currentDateTIme + ".pdf";
        response.setHeader(headerKey, headerValue);
        reportService.exportJasperReports(response);
    }

    @GetMapping("/student/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=students_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<Student> getAllStudent = studentService.getAllStudent();

        StudentExcelExporter excelExporter = new StudentExcelExporter(getAllStudent);

        excelExporter.export(response);
    }

}