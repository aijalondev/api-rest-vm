package com.api_rest_vm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @Schema(description = "O e-mail do usuário") @NotNull(message = "E-mail cannot be null") @NotBlank(message = "E-mail cannot be blank") String email,
        @Schema(description = "A senha do usuário") @NotNull(message = "Password cannot be null") @NotBlank(message = "Password cannot be blank") String password) {
}
