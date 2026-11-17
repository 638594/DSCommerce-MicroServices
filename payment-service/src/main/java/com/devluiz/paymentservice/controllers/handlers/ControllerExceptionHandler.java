package com.devluiz.paymentservice.controllers.handlers;

import com.devluiz.paymentservice.dto.CustomError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/*
 * O payment-service ainda não tinha tratamento de erros,
 * porque até agora ele só recebia mensagens. Agora que o GET /payments exige token,
 * os erros de segurança precisam sair no mesmo formato dos outros serviços.
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    // Sem token, token inválido ou expirado (vem da segurança, repassado pelo SecurityConfig)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CustomError> unauthorized(AuthenticationException e, HttpServletRequest request) {
        return ResponseEntity.status(401).body(new CustomError(401, "Token ausente, inválido ou expirado", request.getRequestURI()));
    }

    // Token válido, mas sem permissão (ex.: Maria listando pagamentos)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomError> accessDenied(AccessDeniedException e, HttpServletRequest request) {
        return ResponseEntity.status(403).body(new CustomError(403, "Acesso negado", request.getRequestURI()));
    }
}