package com.api_rest_vm.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api_rest_vm.dto.RegisterRequest;
import com.api_rest_vm.dto.UserRequest;
import com.api_rest_vm.dto.UserResponse;
import com.api_rest_vm.entity.User;
import com.api_rest_vm.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> searchUsers(
            @RequestParam(value = "name", required = false) String name,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {

        Page<UserResponse> users;

        if (name != null && !name.trim().isEmpty()) {
            users = userService.findUsersByNameContainingIgnoreCase(name, pageable);
        } else {
            users = userService.findAllUsers(pageable);
        }

        return ResponseEntity.ok(users);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest registerRequest) {
        userService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@NotNull @PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
        userService.update(id, userRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
