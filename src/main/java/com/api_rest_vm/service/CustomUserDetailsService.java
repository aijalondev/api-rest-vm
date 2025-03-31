package com.api_rest_vm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.api_rest_vm.entity.User user = userService.findUserByEmail(email);

        UserBuilder builder = User.withUsername(user.getEmail());
        builder.password(user.getPassword());
        builder.authorities(user.getRole().name());

        return builder.build();
    }
}
