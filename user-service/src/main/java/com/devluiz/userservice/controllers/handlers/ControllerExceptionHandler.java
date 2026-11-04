package com.devluiz.userservice.controllers.handlers;

import com.devluiz.userservice.dto.CustomError;
import com.devluiz.userservice.services.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.List;

@ControllerAdvice
public class ControllerExceptionHandler {

    // Email ou senha errados no login.
    // A mensagem é a mesma nos dois casos de propósito: dizer "email não existe"
    // ajudaria um atacante a descobrir quais emails estão cadastrados.
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CustomError> authentication(AuthenticationException e, HttpServletRequest request) {
        return ResponseEntity.status(401).body(new CustomError(401, "Email ou senha inválidos", request.getRequestURI()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomError> notFound(ResourceNotFoundException e, HttpServletRequest request) {
        return ResponseEntity.status(404).body(new CustomError(404, e.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<CustomError.FieldMessage> fields = e.getBindingResult().getFieldErrors().stream()
                .map((FieldError f) -> new CustomError.FieldMessage(f.getField(), f.getDefaultMessage()))
                .toList();
        CustomError err = new CustomError(Instant.now(), 422, "Dados invalidos", request.getRequestURI(), fields);
        return ResponseEntity.status(422).body(err);
    }
}
