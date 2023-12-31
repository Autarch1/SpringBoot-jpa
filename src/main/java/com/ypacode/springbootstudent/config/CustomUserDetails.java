package com.ypacode.springbootstudent.config;

import com.ypacode.springbootstudent.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class CustomUserDetails implements UserDetails  {

       private User user;

        public CustomUserDetails(User user) {
            this.user = user;
        }


    @Override
        public String getUsername() {
            return user.getUserName();
        }

        @Override
        public String getPassword() {
            return user.getUserPassword();
        }

        @Override
        public Collection<? extends  GrantedAuthority> getAuthorities() {
            String roles = user.getUserRole();
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            if(roles.equals("2")) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }else {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }

            return authorities;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return user.isEnabled();
        }

}
