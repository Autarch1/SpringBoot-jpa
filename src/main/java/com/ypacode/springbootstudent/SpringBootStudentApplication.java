package com.ypacode.springbootstudent;

import com.ypacode.springbootstudent.repository.UserRepo;
import com.ypacode.springbootstudent.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
public class SpringBootStudentApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootStudentApplication.class, args);
    }

}
