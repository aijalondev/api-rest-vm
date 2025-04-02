package com.api_rest_vm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
public record UserRequest(
        @Schema(description = "Nome do usuário (opcional)") String name,
        @Schema(description = "E-mail do usuário (opcional)") String email,
        @Schema(description = "Perfil do usuário (opcional)") String role) {
}