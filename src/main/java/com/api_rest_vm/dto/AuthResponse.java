package com.api_rest_vm.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponse(
        @Schema(description = "Token de autenticação") String token,
        @Schema(description = "O perfil ou papel do usuário autenticado") String role,
        @Schema(description = "O identificador único do usuário autenticado no sistema") Long id) {
}