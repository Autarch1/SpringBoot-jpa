package com.ypacode.springbootstudent.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
public class User {
    @Id
    private String userId;
    private String userName;

    @Column(unique = true)
    private String userEmail;

    private String userPassword;
    private String userRole;
    private boolean enabled = false;
//    private String otp; // Add this field to store OTP

}
