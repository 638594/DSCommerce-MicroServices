package com.devluiz.productservice.controllers.handlers;

import com.devluiz.productservice.dto.CustomError;
import com.devluiz.productservice.services.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
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
}
