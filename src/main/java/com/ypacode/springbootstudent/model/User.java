package com.ypacode.springbootstudent.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;


@Entity
@Data
public class User {
    @Id
    private String userId;
    private String userName;
    private String userEmail;
    private String userPassword;
    private String userRole;

}
