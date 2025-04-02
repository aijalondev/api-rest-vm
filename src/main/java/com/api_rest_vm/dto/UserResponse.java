package com.api_rest_vm.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponse(
        @Schema(description = "ID do usuário") Long id,
        @Schema(description = "Nome do usuário") String name,
        @Schema(description = "E-mail do usuário") String email,
        @Schema(description = "Perfil do usuário") String role) {
}