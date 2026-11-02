package com.devluiz.orderservice.services.exceptions;

/* 422: o pedido chegou certinho, mas tem um dado inválido (ex.: produto que não existe). */
public class InvalidOrderException extends RuntimeException {

    public InvalidOrderException(String message) {
        super(message);
    }
}
