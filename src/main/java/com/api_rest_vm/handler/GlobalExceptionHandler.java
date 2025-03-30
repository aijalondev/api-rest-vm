package com.api_rest_vm.handler;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.api_rest_vm.dto.ErrorResponse;
import com.api_rest_vm.exception.AuthenticationException;
import com.api_rest_vm.exception.BadRequestException;
import com.api_rest_vm.exception.InternalServerErrorException;
import com.api_rest_vm.exception.NotFoundException;
import com.api_rest_vm.exception.TokenException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getAllErrors().stream()
                .map(error -> ((FieldError) error).getDefaultMessage())
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), String.join(", ",
                        errorMessages)));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(NotFoundException ex) {
        return buildResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<Object> handleInvalidTokenException(TokenException ex) {
        return buildResponse(ex, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthorizationException(AuthenticationException ex) {
        return buildResponse(ex, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<Object> handleBadRequestException(InternalServerErrorException ex) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> buildResponse(Exception ex, HttpStatus status) {
        String errorMessage = ex.getMessage() != null ? ex.getMessage() : "Error on server";

        ErrorResponse errorResponse = new ErrorResponse(status.value(), errorMessage);

        return new ResponseEntity<>(errorResponse, status);
    }
}