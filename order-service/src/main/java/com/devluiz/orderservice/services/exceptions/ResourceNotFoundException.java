package com.devluiz.orderservice.services.exceptions;

/* 404: o recurso pedido na URL não existe (ex.: GET /orders/999). */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
