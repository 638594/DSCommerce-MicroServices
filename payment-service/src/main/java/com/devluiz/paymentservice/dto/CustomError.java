package com.devluiz.paymentservice.dto;

import java.time.Instant;
import java.util.List;

/*
 * Formato padrão de erro devolvido pela API.
 * "errors" só vem preenchido em erro de validação (422); nos outros casos é uma lista vazia.
 */
public record CustomError(
        Instant timestamp,
        Integer status,
        String error,
        String path,
        List<FieldMessage> errors
) {

    public CustomError(Integer status, String error, String path) {
        this(Instant.now(), status, error, path, List.of());
    }

    public record FieldMessage(String fieldName, String message) {
    }
}
