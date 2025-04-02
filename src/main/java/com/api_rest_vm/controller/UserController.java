package com.api_rest_vm.controller;

import com.api_rest_vm.dto.ErrorResponse;
import com.api_rest_vm.dto.RegisterRequest;
import com.api_rest_vm.dto.UserRequest;
import com.api_rest_vm.dto.UserResponse;
import com.api_rest_vm.entity.User;
import com.api_rest_vm.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@Tag(name = "Usuários", description = "Controlador para gerenciamento de usuários")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "Busca um usuário por ID", description = "Retorna os detalhes de um usuário específico com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })

    public ResponseEntity<Optional<User>> findById(
            @Parameter(description = "ID do usuário a ser buscado", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Busca usuários com paginação e filtro por nome", description = "Retorna uma lista paginada de usuários. Permite filtrar por nome (case-insensitive).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
    })
    public ResponseEntity<Page<UserResponse>> searchUsers(
            @Parameter(description = "Nome para filtrar os usuários (opcional)") @RequestParam(value = "name", required = false) String name,
            @Parameter(hidden = true) @PageableDefault(size = 20, sort = "name") Pageable pageable) {

        Page<UserResponse> users;

        if (name != null && !name.trim().isEmpty()) {
            users = userService.findUsersByNameContainingIgnoreCase(name, pageable);
        } else {
            users = userService.findAllUsers(pageable);
        }

        return ResponseEntity.ok(users);
    }

    @PostMapping("/register")
    @Operation(summary = "Cadastra um novo usuário", description = "Cria um novo usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> register(
            @Valid @Parameter(description = "Objeto com os dados do usuário a serem cadastrado", required = true, schema = @Schema(implementation = RegisterRequest.class)) @RequestBody RegisterRequest registerRequest) {
        userService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um usuário existente", description = "Atualiza os dados de um usuário existente com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> update(
            @Parameter(description = "ID do usuário a ser atualizado", required = true) @NotNull @PathVariable Long id,
            @Parameter(description = "Objeto com os dados do usuário a serem atualizados", required = true, schema = @Schema(implementation = UserRequest.class)) @RequestBody UserRequest userRequest) {
        userService.update(id, userRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um usuário", description = "Exclui um usuário da plataforma com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do usuário a ser excluído", required = true) @NotNull @PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}