package com.api_rest_vm.service;

import com.api_rest_vm.entity.User;
import com.api_rest_vm.exception.NotFoundException;
import com.api_rest_vm.model.Role;
import com.api_rest_vm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

@SpringBootTest
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Aijalon", "aijalon@vm.com", "passwordMock", Role.USER);
    }

    @Test
    // Quando o e-mail existe no banco, o UserDetails deve ser retornado com as
    // credenciais do usuário
    void loadUserByUsername_whenEmailExists_returnsUserDetails() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());

        assertNotNull(userDetails);
        assertEquals(user.getEmail(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(user.getRole().name())));
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    // Quando o e-mail não é encontrado no banco, uma exceção NotFoundException deve
    // ser lançada
    void loadUserByUsername_whenEmailDoesNotExist_throwsNotFoundException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(user.getEmail());
        });

        assertEquals("User not found with e-mail: " + user.getEmail(), exception.getMessage());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }
}