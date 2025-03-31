package com.api_rest_vm.service;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.api_rest_vm.exception.BadRequestException;
import com.api_rest_vm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ValidationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ValidationService validationService;

    @Test
    void validateEmailNotExists_shouldNotThrowException_whenEmailDoesNotExist() {
        String newEmail = "new_email_mock@vm.com";
        when(userRepository.existsByEmail(newEmail)).thenReturn(false);

        validationService.validateEmailNotExists(newEmail);

        verify(userRepository).existsByEmail(newEmail);
    }

    @Test
    void validateEmailNotExists_shouldThrowExceptionWithCorrectMessage_whenEmailExists() {
        String existingEmail = "email_mock@vm.com";
        when(userRepository.existsByEmail(existingEmail)).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            validationService.validateEmailNotExists(existingEmail);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository).existsByEmail(existingEmail);
    }

    @Test
    void validateNameNotExists_shouldNotThrowException_whenEmailDoesNotExist() {
        String newName = "new_name_mock";
        when(userRepository.existsByName(newName)).thenReturn(false);

        validationService.validateNameNotExists(newName);

        verify(userRepository).existsByName(newName);
    }

    @Test
    void validateNameNotExists_shouldThrowException_whenNameExists() {
        String existingName = "exist_name_mock";
        when(userRepository.existsByName(existingName)).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            validationService.validateNameNotExists(existingName);
        });

        assertEquals("Name already exists", exception.getMessage());
        verify(userRepository).existsByName(existingName);
    }
}
