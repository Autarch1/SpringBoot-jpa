package com.ypacode.springbootstudent.service;

import com.ypacode.springbootstudent.model.Student;
import com.ypacode.springbootstudent.model.User;
import com.ypacode.springbootstudent.repository.StudentRepo;
import com.ypacode.springbootstudent.repository.UserRepo;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private StudentRepo studentRepo;
    public void exportJasperReport(HttpServletResponse response) throws JRException, IOException {
        List<User> user = userRepo.findAll();
        //Get file and compile it
        File file = ResourceUtils.getFile("classpath:SpringBoot_User.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(user);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Simplifying Tech");
        //Fill Jasper report
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        //Export report
        JasperExportManager.exportReportToPdfStream(jasperPrint,response.getOutputStream());
    }

    public void exportJasperReports(HttpServletResponse response) throws JRException, IOException {
        List<Student> students = studentRepo.findAll();
        //Get file and compile it
        File files = ResourceUtils.getFile("classpath:Student.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(files.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(students);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Simplifying Tech");
        //Fill Jasper report
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        //Export report
        JasperExportManager.exportReportToPdfStream(jasperPrint,response.getOutputStream());
    }
}
