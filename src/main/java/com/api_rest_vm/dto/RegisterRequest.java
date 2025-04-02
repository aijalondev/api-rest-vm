package com.api_rest_vm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
                @Schema(description = "Nome do usuário") @NotNull(message = "Name cannot be null") @NotBlank(message = "Name cannot be blank") String name,
                @Schema(description = "E-mail do usuário") @NotNull(message = "E-mail cannot be null") @NotBlank(message = "E-mail cannot be blank") String email,
                @Schema(description = "Senha do usuário") @NotNull(message = "Password cannot be null") @NotBlank(message = "Password cannot be blank") String password,
                @Schema(description = "Perfil do usuário") @NotNull(message = "Role cannot be null") @NotBlank(message = "Role cannot be blank") String role) {
}