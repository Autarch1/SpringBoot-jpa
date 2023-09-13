package com.ypacode.springbootstudent.controller;

import com.ypacode.springbootstudent.model.User;
import com.ypacode.springbootstudent.service.UserService;
import com.ypacode.springbootstudent.util.PasswordHash;
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

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {
    @Autowired
    private UserService userService;


    @GetMapping(value = "/")
    public String welocme() {
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

    @GetMapping(value = "/disableUser/{userId}")
        public String disableUser(@PathVariable String userId){
             userService.delete(userId);

                return "redirect:/userList";

        }


    @GetMapping(value = "/userProfile")
    public String profileUpdate(){
        return "UserProfile";
    }
}
