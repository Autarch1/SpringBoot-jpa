package com.ypacode.springbootstudent.repository;

import com.ypacode.springbootstudent.model.Student;
import com.ypacode.springbootstudent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, String> {
    @Query("SELECT MAX(u.userId) FROM User u")
    String findHighestUserId();

    Optional<User> findByUserEmail(String userEmail);
    User findByUserId(String userId);


}
