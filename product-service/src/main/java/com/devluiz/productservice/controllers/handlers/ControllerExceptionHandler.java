package com.devluiz.productservice.controllers.handlers;

import com.devluiz.productservice.dto.CustomError;
import com.devluiz.productservice.services.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.List;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomError> notFound(ResourceNotFoundException e, HttpServletRequest request) {
        CustomError err = new CustomError(404, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(404).body(err);
    }

    // Ex.: tentar apagar uma categoria que ainda tem produtos ligados a ela
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CustomError> database(DataIntegrityViolationException e, HttpServletRequest request) {
        CustomError err = new CustomError(400, "Falha de integridade referencial", request.getRequestURI());
        return ResponseEntity.status(400).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<CustomError.FieldMessage> fields = e.getBindingResult().getFieldErrors().stream()
                .map((FieldError f) -> new CustomError.FieldMessage(f.getField(), f.getDefaultMessage()))
                .toList();
        CustomError err = new CustomError(Instant.now(), 422, "Dados invalidos", request.getRequestURI(), fields);
        return ResponseEntity.status(422).body(err);
    }

    // NOVO na etapa 6b: sem token, token inválido ou expirado (vem da segurança, repassado pelo SecurityConfig)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CustomError> unauthorized(AuthenticationException e, HttpServletRequest request) {
        return ResponseEntity.status(401).body(new CustomError(401, "Token ausente, inválido ou expirado", request.getRequestURI()));
    }

    // NOVO na etapa 6b: token válido, mas sem permissão (ex.: cliente tentando criar produto)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomError> accessDenied(AccessDeniedException e, HttpServletRequest request) {
        return ResponseEntity.status(403).body(new CustomError(403, "Acesso negado", request.getRequestURI()));
    }
}
