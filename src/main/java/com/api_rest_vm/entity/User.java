package com.api_rest_vm.entity;

import lombok.*;

import com.api_rest_vm.model.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador do usuário")
    private Long id;

    @Schema(description = "Nome do usuário")
    private String name;

    @Schema(description = "E-mail do usuário")
    private String email;

    @Schema(description = "Senha do usuário")
    private String password;

    @Enumerated(EnumType.STRING)
    @Schema(description = "O perfil ou papel do usuário")
    private Role role;
}
