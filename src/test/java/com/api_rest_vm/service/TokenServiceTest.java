package com.api_rest_vm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.api_rest_vm.entity.User;
import com.api_rest_vm.exception.TokenException;
import com.api_rest_vm.model.Role;

import static org.mockito.Mockito.*;

import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private UserDetails userDetails;

    @Mock
    private MessageDigest messageDigest;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Aijalon Honasc", "aijalon@vm.com", "passwordMock", Role.USER);

        ReflectionTestUtils.setField(tokenService, "jwtExpirationTime", 1000000L);
        ReflectionTestUtils.setField(tokenService, "jwtSecret", "mySecretKey");
    }

    // Testa se o método generateToken retorna um token válido para um usuário
    // correto
    @Test
    void generateToken_validUser_returnsToken() {
        String response = tokenService.generateToken(user);
        assertNotNull(response);
    }

    // Testa se o método generateToken lança uma TokenException ao falhar na geração
    // da chave secreta
    @Test
    void generateToken_invalidSecretKey_throwsTokenException() throws Exception {
        try (MockedStatic<MessageDigest> mockedDigest = mockStatic(MessageDigest.class)) {
            mockedDigest.when(() -> MessageDigest.getInstance("SHA-256"))
                    .thenThrow(new RuntimeException("Error Mock"));

            TokenException exception = assertThrows(TokenException.class, () -> {
                tokenService.generateToken(user);
            });

            assertEquals("Error generating secret key", exception.getMessage());
        }
    }

    // Testa se o método extractUsername retorna corretamente o nome de usuário do
    // token
    @Test
    void extractUsername_validToken_returnsCorrectUsername() {
        String token = tokenService.generateToken(user);
        String response = tokenService.extractUsername(token);
        assertEquals(user.getEmail(), response);
    }

    // Testa se o método validateToken retorna true para um token válido e
    // correspondente ao usuário
    @Test
    void validateToken_validToken_returnsTrue() {
        String token = tokenService.generateToken(user);
        when(userDetails.getUsername()).thenReturn(user.getEmail());

        boolean response = tokenService.validateToken(token, userDetails);

        assertTrue(response);
    }

    // Testa se o método validateToken retorna false quando o username do token não
    // corresponde ao usuário
    @Test
    void validateToken_invalidUsername_returnsFalse() {
        String token = tokenService.generateToken(user);
        when(userDetails.getUsername()).thenReturn("wrong@vm.com");

        boolean response = tokenService.validateToken(token, userDetails);

        assertFalse(response);
    }
}
