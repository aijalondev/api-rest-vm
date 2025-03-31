package com.api_rest_vm.service;

import org.springframework.stereotype.Service;

import com.api_rest_vm.exception.BadRequestException;
import com.api_rest_vm.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ValidationService {

    private final UserRepository userRepository;

    public void validateEmailNotExists(String newEmail) {
        if (userRepository.existsByEmail(newEmail)) {
            throw new BadRequestException("Email already exists");
        }
    }

    public void validateNameNotExists(String newName) {
        if (userRepository.existsByName(newName)) {
            throw new BadRequestException("Name already exists");
        }
    }
}