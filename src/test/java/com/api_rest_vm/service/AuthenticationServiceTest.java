package com.api_rest_vm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.api_rest_vm.dto.AuthResponse;
import com.api_rest_vm.dto.LoginRequest;
import com.api_rest_vm.entity.User;
import com.api_rest_vm.exception.AuthenticationException;
import com.api_rest_vm.exception.NotFoundException;
import com.api_rest_vm.model.Role;

@SpringBootTest
class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        
        Role role = Role.USER;
        
        user = new User(1L, "Aijalon Honasc", "aijalon@vm", "passwordMock", role);

        loginRequest = new LoginRequest("aijalon@vm", "passwordMock");
    }

    // Testa autenticação bem-sucedida quando o usuário existe e a senha está
    // correta
    @Test
    void authenticate_whenCredentialsAreValid_returnsAuthResponse() {
        when(userService.findUserByEmail(loginRequest.email())).thenReturn(user);
        when(tokenService.generateToken(user)).thenReturn("tokenMock");
        when(passwordEncoder.matches(loginRequest.password(), user.getPassword())).thenReturn(true);

        AuthResponse response = authenticationService.authenticate(loginRequest);

        assertNotNull(response);
        assertEquals("tokenMock", response.token());
    }

    // Testa exceção quando o usuário não é encontrado no banco
    @Test
    void authenticate_whenUserNotFound_throwsNotFoundException() {
        when(userService.findUserByEmail(loginRequest.email()))
                .thenThrow(new NotFoundException("User not found with email: " + loginRequest.email()));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> authenticationService.authenticate(loginRequest));

        assertEquals("User not found with email: " + loginRequest.email(), exception.getMessage());
    }

    // Testa exceção quando a senha fornecida é inválida
    @Test
    void authenticate_whenPasswordIsIncorrect_throwsAuthenticationException() {
        when(userService.findUserByEmail(loginRequest.email())).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", user.getPassword())).thenReturn(false);

        // Simula senha incorreta
        LoginRequest wrongPasswordRequest = new LoginRequest("aijalon@vm", "wrongPassword");

        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> authenticationService.authenticate(wrongPasswordRequest));

        assertEquals("Invalid e-mail or password", exception.getMessage());
    }
}