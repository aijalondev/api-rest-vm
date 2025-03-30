package com.api_rest_vm.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api_rest_vm.dto.AuthResponse;
import com.api_rest_vm.dto.LoginRequest;
import com.api_rest_vm.entity.User;
import com.api_rest_vm.exception.AuthenticationException;
import com.api_rest_vm.exception.NotFoundException;
import com.api_rest_vm.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse authenticate(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + loginRequest.email()));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new AuthenticationException("Invalid e-mail or password");
        }

        return new AuthResponse(tokenService.generateToken(user));
    }
}
