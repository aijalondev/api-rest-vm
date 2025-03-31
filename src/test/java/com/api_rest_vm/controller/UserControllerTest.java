package com.api_rest_vm.controller;

import com.api_rest_vm.dto.RegisterRequest;
import com.api_rest_vm.dto.UserRequest;
import com.api_rest_vm.dto.UserResponse;
import com.api_rest_vm.entity.User;
import com.api_rest_vm.exception.BadRequestException;
import com.api_rest_vm.exception.NotFoundException;
import com.api_rest_vm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private Long userId;
    private User user;
    private RegisterRequest registerRequest;
    private UserRequest userRequest;
    private Pageable pageable;
    private List<UserResponse> userResponseList;
    private Page<UserResponse> userResponsePage;

    @BeforeEach
    void setUp() {
        userId = 1L;
        user = new User();
        user.setId(userId);
        user.setName("nameMock");
        user.setEmail("aijalon@vm.com");

        registerRequest = new RegisterRequest("nameMock", "new@vm.com", "password", "USER");
        userRequest = new UserRequest("nameMock", "updated@vm.com", "ADMIN");

        pageable = Pageable.ofSize(20);
        userResponseList = List.of(new UserResponse(1L, "User 1", "email1", "USER"),
                new UserResponse(2L, "User 2", "email2", "ADMIN"));
        userResponsePage = new PageImpl<>(userResponseList, pageable, userResponseList.size());
    }

    // Verifica se, ao buscar um usuário com um ID existente, o controlador retorna
    // corretamente os dados do usuário
    @Test
    void findById_existingId_returnsOkWithUser() {
        when(userService.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<Optional<User>> response = userController.findById(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isPresent();
        assertThat(response.getBody().get().getId()).isEqualTo(userId);
    }

    // Verifica se, ao buscar um usuário com um ID inexistente, o controlador
    // retorna uma resposta vazia
    @Test
    void findById_nonExistingId_returnsOkWithEmptyOptional() {
        when(userService.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<Optional<User>> response = userController.findById(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    // Testa se, ao buscar usuários sem fornecer o nome, o controlador retorna todos
    // os usuários cadastrados
    @Test
    void searchUsers_noNameParam_returnsOkWithAllUsers() {
        when(userService.findAllUsers(pageable)).thenReturn(userResponsePage);

        ResponseEntity<Page<UserResponse>> response = userController.searchUsers(null, pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(userResponsePage);
        verify(userService, times(1)).findAllUsers(pageable);
        verify(userService, never()).findUsersByNameContainingIgnoreCase(anyString(), any(Pageable.class));
    }

    // Verifica se, ao buscar usuários com um parâmetro 'name' vazio, o controlador
    // ainda retorna todos os usuários
    @Test
    void searchUsers_withNameParamEmpty_returnsOkWithAllUsers() {
        when(userService.findAllUsers(pageable)).thenReturn(userResponsePage);

        ResponseEntity<Page<UserResponse>> response = userController.searchUsers("", pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(userResponsePage);
        verify(userService, times(1)).findAllUsers(pageable);
        verify(userService, never()).findUsersByNameContainingIgnoreCase(anyString(), any(Pageable.class));
    }

    // Testa se, ao buscar usuários com um nome específico, o controlador retorna
    // apenas aqueles que contêm esse nome
    @Test
    void searchUsers_withNameParam_returnsOkWithUsersByName() {
        String name = "User";
        when(userService.findUsersByNameContainingIgnoreCase(name, pageable)).thenReturn(userResponsePage);

        ResponseEntity<Page<UserResponse>> response = userController.searchUsers(name, pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(userResponsePage);
        verify(userService, never()).findAllUsers(pageable);
        verify(userService, times(1)).findUsersByNameContainingIgnoreCase(name, pageable);
    }

    // Verifica se, ao tentar registrar um novo usuário com uma requisição válida, o
    // controlador retorna um status de criação com sucesso
    @Test
    void register_validRequest_returnsCreated() {
        ResponseEntity<Void> response = userController.register(registerRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(userService, times(1)).register(registerRequest);
    }

    // Testa fazer cadastro de um usuário com E-mail já existente, o controlador
    // deve retornar um status de erro (bad request)
    @Test
    void register_existsUserWithEmail_returnsBadRequest() {

        doThrow(new BadRequestException("User not found")).when(userService).register(registerRequest);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userController.register(registerRequest);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userService, times(1)).register(registerRequest);
    }

    // Verifica se, ao atualizar um usuário existente com uma requisição válida, o
    // controlador retorna um status de sucesso
    @Test
    void update_validRequest_returnsOk() {
        ResponseEntity<Void> response = userController.update(userId, userRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userService, times(1)).update(userId, userRequest);
    }

    // Testa fazer atualização de um usuário com ID inexistente, o controlador
    // deve retornar um status de erro (não encontrado)
    @Test
    void update_nonExistingId_returnsNotFound() {

        doThrow(new BadRequestException("User not found")).when(userService).delete(userId);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userController.delete(userId);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userService, times(1)).delete(userId);
    }

    // Testa fazer uma exclusão de um usuário com ID existente, o controlador
    // deve retornar um status de sucesso
    @Test
    void delete_existingId_returnsOk() {
        ResponseEntity<Void> response = userController.delete(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userService, times(1)).delete(userId);
    }

    // Testa fazer uma exclusão de um usuário com ID inexistente, o controlador
    // deve retornar um status de erro (não encontrado)
    @Test
    void delete_nonExistingId_returnsNotFound() {

        doThrow(new NotFoundException("User not found")).when(userService).delete(userId);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userController.delete(userId);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userService, times(1)).delete(userId);
    }
}