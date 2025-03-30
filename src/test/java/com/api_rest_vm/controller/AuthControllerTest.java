package com.api_rest_vm.controller;

import com.api_rest_vm.dto.AuthResponse;
import com.api_rest_vm.dto.LoginRequest;
import com.api_rest_vm.exception.AuthenticationException;
import com.api_rest_vm.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationService authService;

    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("aijalon@vm.com", "passwordMock");
        authResponse = new AuthResponse("tokenMock");
    }

    // Testa o comportamento do método `login` quando as credenciais fornecidas pelo
    // usuário são corretas
    // Verifica se o controlador de autenticação retorna uma resposta de
    // autenticação (AuthResponse) com sucesso.
    @Test
    void login_whenCredentialsAreCorrect_returnsAuthResponse() {
        when(authService.authenticate(loginRequest)).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        assertNotNull(response);
        assertEquals(authResponse, response.getBody());
        verify(authService, times(1)).authenticate(loginRequest);
    }

    // Testa o comportamento do método `login` quando as credenciais fornecidas pelo
    // usuário são incorretas
    // Verifica se o controlador lança uma AuthenticationException com a mensagem
    // correta quando o serviço de autenticação falha.
    @Test
    void login_whenCredentialsAreIncorrect_throwsAuthenticationException() {
        when(authService.authenticate(loginRequest))
                .thenThrow(new AuthenticationException("Invalid email or password"));

        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            authController.login(loginRequest);
        });

        assertEquals("Invalid email or password", exception.getMessage());
        verify(authService, times(1)).authenticate(loginRequest);
    }
}