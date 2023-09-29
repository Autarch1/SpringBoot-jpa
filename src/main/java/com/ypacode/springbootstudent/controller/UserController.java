package com.ypacode.springbootstudent.controller;

import com.ypacode.springbootstudent.model.User;
import com.ypacode.springbootstudent.service.CourseService;
import com.ypacode.springbootstudent.service.ReportService;
import com.ypacode.springbootstudent.service.StudentService;
import com.ypacode.springbootstudent.service.UserService;
import com.ypacode.springbootstudent.util.PasswordHash;
import com.ypacode.springbootstudent.util.UserExcelExporter;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private CourseService courseService;
    @GetMapping(value = "/")
    public String welcome(ModelMap model) {
        long studentCount = studentService.getAllStudent().size(); // Get student count using StudentService
        long userCount =userService.getAllUser().size();
        long courseCount = courseService.getAllCourses().size();
        model.addAttribute("studentCount", studentCount);
        model.addAttribute("userCount", userCount);
        model.addAttribute("courseCount", courseCount);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.isAuthenticated()) {
            // Get the username from the authentication object
            String username = authentication.getName();
            model.addAttribute("username", username);
        }
        return "Welcome";
    }


    @GetMapping(value = "/UserRegister")
    public ModelAndView registerView(ModelMap model) {
        User user = new User();
        String nextUserId = userService.generateNextUserId();
        user.setUserId(nextUserId);
        return new ModelAndView("UserRegister", "registerBean", user);
    }

    @PostMapping(value = "ProcessRegister")
    public String register(@ModelAttribute("registerBean") @Validated User ub, BindingResult br,
                           @RequestParam("cPassword") String cPassword,
                           ModelMap model) {
        if (br.hasErrors()) {
            System.out.println("failed");
            return "UserRegister";
        }
        boolean isSamePsw = false;
        String nextUserId = userService.generateNextUserId();
        ub.setUserId(nextUserId);
        if (ub.getUserId().equals("") || ub.getUserName().equals("") || ub.getUserEmail().equals("") ||
                ub.getUserRole().equals("") || cPassword.equals("")) {
            model.addAttribute("blank", "Field cannot be blank");

            return "UserRegister";
        }

        if (userService.isEmailInUse(ub.getUserEmail())) {

            model.addAttribute("sameEmail", "Email is already in use");
            return "UserRegister";
        }

        if (ub.getUserPassword().equals(cPassword)) {
            isSamePsw = true;
            ub.setUserPassword(PasswordHash.hashPassword(ub.getUserPassword()));
            int i = userService.save(ub);
            if (i == 0) {

                model.addAttribute("insertError", "Register Failed");
                return "UserRegister";
            }

        }
        if (!isSamePsw) {

            model.addAttribute("password", "password isn't same");
            return "UserRegister";
        }


        return "redirect:/login";
    }



    @GetMapping(value = "/login")
    public String loginView() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        return "redirect:/";
    }

    @GetMapping(value = "/userList")
    public String usersView(ModelMap model) {
        List<User> userList = userService.getAllUser();
        model.addAttribute("userList", userList);
        return "UserList";

    }

    @GetMapping(value = "/userUpdate/{userId}")
    public ModelAndView userUpdateView(@PathVariable String userId, ModelMap model) {
        User selectedUser = userService.getUserById(userId);
        model.addAttribute("selectedUser", selectedUser);
        return new ModelAndView("UserUpdate", "userUpdate", selectedUser);
    }

    @PostMapping(value = "/UserUpdateProcess")
    public String userUpdate(@ModelAttribute("userUpdate") @Validated User ub, BindingResult br, ModelMap model,
                             @RequestParam("cPassword") String cPassword) {

        if (ub.getUserId().equals("") || ub.getUserName().equals("") || ub.getUserEmail().equals("") ||
                ub.getUserRole().equals("") || cPassword.equals("")) {
            model.addAttribute("blank", "Field cannot be blank");
            return "/userUpdate/" + ub.getUserId();

        }

        if (ub.getUserPassword().equals(cPassword)) {

            ub.setUserPassword(PasswordHash.hashPassword(ub.getUserPassword()));
            int i = userService.update(ub);
            if (i == 0) {
                model.addAttribute("insertError", "Register Failed");
                return "/userUpdate/" + ub.getUserId();
            }
        }else{
            model.addAttribute("passowrd", "password isn't same");
            return "/userUpdate/" + ub.getUserId();
        }



            return "redirect:/userList";


    }

    @GetMapping(value = "/toggleUserEnabled/{userId}")
        public String disableUser(@PathVariable String userId){
             userService.toggleUserEnabled(userId);
                return "redirect:/userList";
        }


    @GetMapping(value = "/profileUpdate/{userId}")
    public ModelAndView profileUpdateView(@PathVariable String userId, ModelMap model) {
        User selectedUser = userService.getUserById(userId);
        model.addAttribute("selectedUser", selectedUser);
        return new ModelAndView("UserUpdate", "profileUpdate", selectedUser);
    }

    @PostMapping(value = "/profileUpdateProcess")
    public String profileUpdate(@ModelAttribute("profileUpdate") @Validated User ub, BindingResult br, ModelMap model,
                             @RequestParam("cPassword") String cPassword) {

        if (ub.getUserId().equals("") || ub.getUserName().equals("") || ub.getUserEmail().equals("") ||
                ub.getUserRole().equals("") || cPassword.equals("")) {
            model.addAttribute("blank", "Field cannot be blank");
            return "/userUpdate/" + ub.getUserId();

        }

        if (ub.getUserPassword().equals(cPassword)) {

            ub.setUserPassword(PasswordHash.hashPassword(ub.getUserPassword()));
            int i = userService.update(ub);
            if (i == 0) {
                model.addAttribute("insertError", "Register Failed");
                return "/userUpdate/" + ub.getUserId();
            }
        }else{
            model.addAttribute("passowrd", "password isn't same");
            return "/userUpdate/" + ub.getUserId();
        }



        return "redirect:/userList";


    }

    @GetMapping(value = "/jasperpdf/export")
    public void createPDF(HttpServletResponse response) throws IOException, JRException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyy-MM-dd:hh:mm:ss");
        String currentDateTIme = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue ="attachment; filename=pdf_" + currentDateTIme + ".pdf";
        response.setHeader(headerKey, headerValue);
        reportService.exportJasperReport(response);
    }

    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<User> listUsers = userService.getAllUser();

        UserExcelExporter excelExporter = new UserExcelExporter(listUsers);

        excelExporter.export(response);
    }


}
