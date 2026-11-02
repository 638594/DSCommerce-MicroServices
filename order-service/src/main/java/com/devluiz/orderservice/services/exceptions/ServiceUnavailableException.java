package com.devluiz.orderservice.services.exceptions;

/* 503: outro serviço do qual este depende está fora do ar. */
public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException(String message) {
        super(message);
    }
}
