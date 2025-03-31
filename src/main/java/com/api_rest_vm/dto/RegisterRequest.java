package com.api_rest_vm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @NotNull(message = "Name cannot be null") @NotBlank(message = "Name cannot be blank") String name,
        @NotNull(message = "E-mail cannot be null") @NotBlank(message = "E-mail cannot be blank") String email,
        @NotNull(message = "Password cannot be null") @NotBlank(message = "Password cannot be blank") String password,
        @NotNull(message = "Role cannot be null") @NotBlank(message = "Role cannot be blank") String role) {
}
