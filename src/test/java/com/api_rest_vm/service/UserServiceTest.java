package com.api_rest_vm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;

import com.api_rest_vm.dto.RegisterRequest;
import com.api_rest_vm.dto.UserRequest;
import com.api_rest_vm.dto.UserResponse;
import com.api_rest_vm.entity.User;
import com.api_rest_vm.exception.BadRequestException;
import com.api_rest_vm.exception.InternalServerErrorException;
import com.api_rest_vm.exception.NotFoundException;
import com.api_rest_vm.model.Role;
import com.api_rest_vm.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private ValidationService validationService;

    private User user;
    private RegisterRequest registerRequest;
    private UserRequest userRequest;
    private Pageable pageable;
    private Page<User> userPage;
    private Long userId;

    @BeforeEach
    void setUp() {

        user = new User(userId, "Aijalon Honasc", "aijalon@vm.com", "passwordMock", Role.USER);

        registerRequest = new RegisterRequest("Aijalon Honasc", "aijalon@vm.com", "passwordMock", "USER");

        userRequest = new UserRequest("Updated Aijalon Honasc", "aijalon_updated@vm.com", "ADMIN");

        pageable = PageRequest.of(0, 20, Sort.by("name"));

        userPage = new PageImpl<>(Collections.singletonList(user), pageable, 1);

        userId = 1L;
    }

    // Testa se o método findById retorna um usuário quando ele existe no banco
    @Test
    void findById_whenUserExists_returnsResponse() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> response = userService.findById(userId);

        assertTrue(response.isPresent());
        assertEquals("Aijalon Honasc", response.get().getName());
    }

    // Testa se o método findById retorna um Optional vazio quando o usuário não
    // existe
    @Test
    void findById_whenUserDoesNotExist_returnsEmptyResponse() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> response = userService.findById(userId);

        assertFalse(response.isPresent());
    }

    // Testa se o método findAllUsers retorna uma lista de usuários quando há
    // usuários cadastrados
    @Test
    void findAllUsers_whenUsersExist_returnsResponse() {
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<UserResponse> response = userService.findAllUsers(pageable);

        assertFalse(response.isEmpty());
        assertEquals(1, response.getTotalElements());
    }

    // Testa se o método findUsersByNameContainingIgnoreCase retorna usuários com
    // nome correspondente
    @Test
    void findUsersByNameContainingIgnoreCase_whenUsersExist_returnsResponse() {
        when(userRepository.findByNameContainingIgnoreCase("Aijalon Honasc", pageable)).thenReturn(userPage);

        Page<UserResponse> response = userService.findUsersByNameContainingIgnoreCase("Aijalon Honasc", pageable);

        assertFalse(response.isEmpty());
        assertEquals(1, response.getTotalElements());
    }

    // Testa se o método register salva o usuário e envia um e-mail ao cadastrar com
    // sucesso
    @Test
    void register_whenSuccessful_savesUserAndSendsEmail() {
        doNothing().when(validationService).validateEmailNotExists(registerRequest.email());
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertDoesNotThrow(() -> userService.register(registerRequest));
        verify(emailService, times(1)).sendEmail(eq(registerRequest.email()), anyString(), anyString());
    }

    // Testa se o método register lança uma exceção quando o e-mail já está
    // cadastrado
    @Test
    void register_whenEmailAlreadyExists_throwsBadRequestException() {
        doThrow(new BadRequestException("User already registered with email: " + registerRequest.email()))
                .when(validationService).validateEmailNotExists(registerRequest.email());

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> userService.register(registerRequest));

        assertEquals("User already registered with email: " + registerRequest.email(), exception.getMessage());
    }

    // Testa se o método register lança uma exceção quando o envio de e-mail falha
    @Test
    void register_whenEmailServiceFails_throwsInternalServerErrorException() {
        doNothing().when(validationService).validateEmailNotExists(registerRequest.email());
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        doThrow(new RuntimeException("Email error")).when(emailService).sendEmail(anyString(), anyString(),
                anyString());

        InternalServerErrorException exception = assertThrows(InternalServerErrorException.class,
                () -> userService.register(registerRequest));

        assertEquals("Failed to send register email to user", exception.getMessage());
    }

    // Testa se o método update atualiza os dados do usuário e envia um e-mail de
    // confirmação
    @Test
    void update_whenUserExists_updatesUserAndSendsEmail() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        doNothing().when(validationService).validateEmailNotExists(registerRequest.email());
        doNothing().when(validationService).validateNameNotExists(userRequest.name());

        assertDoesNotThrow(() -> userService.update(userId, userRequest));

        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendEmail(eq(userRequest.email()), anyString(), anyString());
    }

    // Testa se o método update lança NotFoundException quando o usuário não existe
    @Test
    void update_whenUserDoesNotExist_throwsNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.update(userId, userRequest));

        assertEquals("User not found with id: " + userId, exception.getMessage());
    }

    // Testa se o método update lança uma exceção quando o envio de e-mail falha
    // após a atualização
    @Test
    void update_whenEmailServiceFails_throwsInternalServerErrorException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(validationService).validateEmailNotExists(registerRequest.email());
        doNothing().when(validationService).validateNameNotExists(userRequest.name());

        doThrow(new RuntimeException("Email error")).when(emailService).sendEmail(anyString(), anyString(),
                anyString());

        InternalServerErrorException exception = assertThrows(InternalServerErrorException.class,
                () -> userService.update(userId, userRequest));

        assertEquals("Failed to send update email to user with id: " + userId, exception.getMessage());
    }

    @Test
    void delete_whenUserExists_shouldDeleteUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.delete(userId);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void delete_whenUserDoesNotExist_shouldThrowNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.delete(userId));

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository, never()).delete(any());
    }

    @Test
    void findUserByEmail_whenUserExists_shouldReturnUser() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User foundUser = userService.findUserByEmail(user.getEmail());

        assertNotNull(foundUser);
        assertEquals(user.getEmail(), foundUser.getEmail());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void findUserByEmail_whenUserDoesNotExist_shouldThrowNotFoundException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.findUserByEmail(user.getEmail()));

        assertEquals("User not found with e-mail: " + user.getEmail(), exception.getMessage());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }
}