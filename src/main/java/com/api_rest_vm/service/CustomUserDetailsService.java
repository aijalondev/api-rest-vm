package com.api_rest_vm.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.api_rest_vm.exception.NotFoundException;
import com.api_rest_vm.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.api_rest_vm.entity.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with e-mail: " + email));

        UserBuilder builder = User.withUsername(user.getEmail());
        builder.password(user.getPassword());
        builder.authorities(user.getRole().name());

        return builder.build();
    }
}
