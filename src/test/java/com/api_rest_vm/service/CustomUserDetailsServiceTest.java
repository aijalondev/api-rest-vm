package com.api_rest_vm.service;

import com.api_rest_vm.entity.User;
import com.api_rest_vm.exception.NotFoundException;
import com.api_rest_vm.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CustomUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Aijalon", "aijalon@vm.com", "passwordMock", Role.USER);
    }

    // Testa quando o e-mail existe no banco, o UserDetails deve ser retornado com
    // as
    // credenciais do usuário
    @Test
    void loadUserByUsername_whenEmailExists_returnsUserDetails() {
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());

        assertNotNull(userDetails);
        assertEquals(user.getEmail(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(user.getRole().name())));
        verify(userService, times(1)).findUserByEmail(user.getEmail());
    }

    // Testa quando o e-mail não é encontrado no banco, uma exceção
    // NotFoundException deve
    // ser lançada
    @Test
    void loadUserByUsername_whenEmailDoesNotExist_throwsNotFoundException() {
        when(userService.findUserByEmail(user.getEmail()))
                .thenThrow(new NotFoundException("User not found with e-mail: " + user.getEmail()));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(user.getEmail()));

        assertEquals("User not found with e-mail: " + user.getEmail(), exception.getMessage());
        verify(userService, times(1)).findUserByEmail(user.getEmail());
    }
}