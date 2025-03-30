package com.api_rest_vm.repository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.api_rest_vm.entity.User;
import com.api_rest_vm.model.Role;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        user1 = new User(null, "Aijalon Honasc", "aijalon@vm.com", "passwordMock", Role.USER);
        user2 = new User(null, "Ana Ellen Honasc ", "ellen@vm.com", "passwordMock", Role.ADMIN);
        user3 = new User(null, "Ellen Silva", "ellen.silva@vm.com", "passwordMock", Role.USER);

        // Salvo alguns usuários para conseguir realizar os demais testes
        userRepository.saveAll(List.of(user1, user2, user3));
    }

    // Testa quando alguém tenta encontrar um usuário usando um e-mail que já existe
    // no sistema.
    @Test
    void findByEmail_whenEmailExists_returnsUser() {
        Optional<User> response = userRepository.findByEmail(user1.getEmail());
        assertThat(response).isPresent();
        assertThat(response).get().isEqualTo(user1);
    }

    // Testa quando alguém tenta encontrar um usuário usando um e-mail que não está
    // cadastrado no sistema.
    @Test
    void findByEmail_whenEmailDoesNotExist_returnsEmptyOptional() {
        Optional<User> response = userRepository.findByEmail("naoexite@vm.com");
        assertThat(response).isEmpty();
    }

    // Testa quando alguém está tentando usar um e-mail que já foi utilizado por
    // outro usuário no sistema.
    @Test
    void existsByEmail_whenEmailExists_returnsTrue() {
        boolean response = userRepository.existsByEmail(user1.getEmail());
        assertThat(response).isTrue();
    }

    // Testa quando alguém está tentando usar um e-mail que ainda não foi
    // cadastrado no sistema.
    @Test
    void existsByEmail_whenEmailDoesNotExist_returnsFalse() {
        boolean response = userRepository.existsByEmail("naoexite@vm.com");
        assertThat(response).isFalse();
    }

    // Testa quando alguém pesquisa por usuários usando um nome (ou parte dele)
    // que corresponde a um ou mais usuários no sistema.
    @Test
    void findByNameContainingIgnoreCase_whenNameExists_returnsPageOfUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> usersPage = userRepository.findByNameContainingIgnoreCase("Ellen", pageable);
        assertThat(usersPage).isNotEmpty();
        assertThat(usersPage.getTotalElements()).isEqualTo(2);
        assertThat(usersPage.getContent()).contains(user2, user3);
    }

    // Especificamente, testa quando a busca por nome funciona corretamente quando a
    // pessoa digita o nome com letras maiúsculas e minúsculas de forma diferente do
    // que está cadastrado.
    @Test
    void findByNameContainingIgnoreCase_whenNameExistsWithDifferentCase_returnsPageOfUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> usersPage = userRepository.findByNameContainingIgnoreCase("eLlEn", pageable);
        assertThat(usersPage).isNotEmpty();
        assertThat(usersPage.getTotalElements()).isEqualTo(2);
        assertThat(usersPage.getContent()).contains(user2, user3);
    }

    // Testa quando alguém pesquisa por um nome que não corresponde a nenhum usuário
    // no sistema.
    @Test
    void findByNameContainingIgnoreCase_whenNameDoesNotExist_returnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> usersPage = userRepository.findByNameContainingIgnoreCase("NaoExiste", pageable);
        assertThat(usersPage).isEmpty();
    }

    // Adiciona mais usuários para testar a paginação
    @Test
    void findByNameContainingIgnoreCase_paginationWorksCorrectly() {
        User user4 = new User(null, "Ana Bravo", "ana.bravo@vm.com", "passwordMock", Role.USER);
        User user5 = new User(null, "Ana Clara", "ana.clara@vm.com", "passwordMock", Role.USER);
        userRepository.saveAll(List.of(user4, user5));

        Pageable pageablePage1 = PageRequest.of(0, 2);
        Page<User> usersPage1 = userRepository.findByNameContainingIgnoreCase("Ana", pageablePage1);
        assertThat(usersPage1.getTotalElements()).isEqualTo(3);
        assertThat(usersPage1.getTotalPages()).isEqualTo(2);
        assertThat(usersPage1.getContent()).contains(user2);
    }
}
