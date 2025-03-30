package com.api_rest_vm.service;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api_rest_vm.dto.RegisterRequest;
import com.api_rest_vm.dto.UserRequest;
import com.api_rest_vm.dto.UserResponse;
import com.api_rest_vm.entity.User;
import com.api_rest_vm.exception.BadRequestException;
import com.api_rest_vm.exception.InternalServerErrorException;
import com.api_rest_vm.exception.NotFoundException;
import com.api_rest_vm.model.Role;
import com.api_rest_vm.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Page<UserResponse> findAllUsers(Pageable pageable) {

        Page<User> users = userRepository.findAll(pageable);
        return converterPageUserToPageUserResponse(users);
    }

    public Page<UserResponse> findUsersByNameContainingIgnoreCase(String name, Pageable pageable) {
        Page<User> users = userRepository.findByNameContainingIgnoreCase(name, pageable);
        return converterPageUserToPageUserResponse(users);
    }

    private UserResponse converterUser(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().toString());
    }

    public Page<UserResponse> converterPageUserToPageUserResponse(Page<User> users) {
        return new PageImpl<>(
                users.getContent().stream()
                        .map(this::converterUser)
                        .collect(Collectors.toList()),
                users.getPageable(),
                users.getTotalElements());
    }

    public void register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new BadRequestException(
                    "User already registered with email: " + registerRequest.email());
        }

        Role role = Role.valueOf(registerRequest.role().toUpperCase());

        User user = new User(null, registerRequest.name(), registerRequest.email(),
                passwordEncoder.encode(registerRequest.password()), role);
        userRepository.save(user);

        try {
            emailService.enviarEmail(registerRequest.email(), "Bem vindo à VM Tecnologia",
                    "Cadastro realizado com sucesso!");
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to send register email to user with id: " + user.getId());
        }
    }

    public void update(Long id, UserRequest userRequest) {
        Optional<User> optionalUser = findById(id);

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();

            existingUser.setName(userRequest.name());
            existingUser.setEmail(userRequest.email());
            existingUser.setRole(Role.valueOf(userRequest.role().toUpperCase()));

            userRepository.save(existingUser);

            try {
                emailService.enviarEmail(userRequest.email(), "Atualização Cadastral",
                        "O seus dados foram atualizados com sucesso!");
            } catch (Exception e) {
                throw new InternalServerErrorException("Failed to send update email to user with id: " + id);
            }
        } else {
            throw new NotFoundException("User not found with id: " + id);
        }
    }
}
