package com.devluiz.orderservice.controllers.handlers;

import com.devluiz.orderservice.dto.CustomError;
import com.devluiz.orderservice.services.exceptions.InvalidOrderException;
import com.devluiz.orderservice.services.exceptions.ResourceNotFoundException;
import com.devluiz.orderservice.services.exceptions.ServiceUnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.amqp.AmqpException;
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
        return ResponseEntity.status(404).body(new CustomError(404, e.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<CustomError> invalidOrder(InvalidOrderException e, HttpServletRequest request) {
        return ResponseEntity.status(422).body(new CustomError(422, e.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<CustomError> unavailable(ServiceUnavailableException e, HttpServletRequest request) {
        return ResponseEntity.status(503).body(new CustomError(503, e.getMessage(), request.getRequestURI()));
    }

    // NOVO na etapa 3: RabbitMQ fora do ar na hora de publicar a mensagem.
    // Como a exceção sai de dentro do @Transactional, o pedido NÃO é salvo:
    // assim nunca fica um pedido sem a mensagem de pagamento correspondente.
    @ExceptionHandler(AmqpException.class)
    public ResponseEntity<CustomError> messaging(AmqpException e, HttpServletRequest request) {
        CustomError err = new CustomError(503, "Serviço de mensagens indisponível. Tente novamente em instantes.", request.getRequestURI());
        return ResponseEntity.status(503).body(err);
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
