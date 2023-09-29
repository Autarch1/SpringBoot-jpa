package com.ypacode.springbootstudent.config;

import com.ypacode.springbootstudent.repository.UserRepo;
import com.ypacode.springbootstudent.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepo userRepo;


    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .requestMatchers("/UserRegister", "/ProcessRegister", "/verify-otpP").permitAll()
                                .requestMatchers("/courseRegister","/ProcessCourseRegister", "/studentRegister", "/StudentRegisterProcess", "/studentList", "/userList","/studentPhoto", "/studentUpdate/**", "/StudentUpdateProcess", "/userUpdate/**", "/UserUpdateProcess"
                                ,"/toggleUserEnabled/**","/disableStudent/**","/jasperpdf/export", "/jasper-pdf/export", "/users/export/excel", "/student/export/excel", "/courseList").hasAnyRole("ADMIN","USER")
                                .requestMatchers("/studentRegister").hasRole("USER")
                                .requestMatchers("/", "/profileUpdate", "/profileUpdateProcess", "/studentRegister", "/StudentRegisterProcess", "/studentList", "/userList").hasAnyRole("USER", "ADMIN")

                ).formLogin(form ->
                        form
                                .loginPage("/login")
                                .usernameParameter("email")
                                .loginProcessingUrl("/processLogin")
                                .successHandler((request, response, authentication) -> {
                                    // Redirect based on user's role
                                    if (authentication.getAuthorities().stream()
                                            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
                                        response.sendRedirect("/userList");
                                    } else if (authentication.getAuthorities().stream()
                                            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"))) {
                                        response.sendRedirect("/");
                                    }
                                })
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll()
                ).sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(true)
                );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserService(userRepo);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {
            web.ignoring().requestMatchers("/resources/**","/SpringBoot_User.jrxml","/Student.jrxml"); // Ignore static resources
        };
    }
}