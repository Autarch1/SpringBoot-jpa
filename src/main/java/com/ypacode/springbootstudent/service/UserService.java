package com.ypacode.springbootstudent.service;

import com.ypacode.springbootstudent.config.CustomUserDetails;
import com.ypacode.springbootstudent.model.User;
import com.ypacode.springbootstudent.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public List<User> getAllUser() {
        return userRepo.findAll();
    }

    public User getUserById(String userId) {
        return userRepo.findByUserId(userId);
    }

    public int save(User user) {
        user.setUserId(generateNextUserId());
        try {
            userRepo.save(user);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void toggleUserEnabled(String userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setEnabled(!user.isEnabled()); // Toggle the enabled status
        userRepo.save(user);
    }

    public int update(User user) {
        try {
            userRepo.save(user);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String generateNextUserId() {
        String highestUserId = userRepo.findHighestUserId();

        if (highestUserId == null) {
            return "USR001";
        } else {
            int numericPart = Integer.parseInt(highestUserId.substring(3)) + 1;
            return String.format("USR%03d", numericPart);
        }
    }

    public boolean isEmailInUse(String email) {
        Optional<User> existingUser = userRepo.findByUserEmail(email);
        return existingUser.isPresent();
    }

    public Optional<User> getUserByEmail(String userEmail) {
        return userRepo.findByUserEmail(userEmail);
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findByUserEmail(userEmail);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        return new CustomUserDetails(user.get());
    }


}
