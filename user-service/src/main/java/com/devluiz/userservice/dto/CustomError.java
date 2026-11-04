package com.devluiz.userservice.dto;

import java.time.Instant;
import java.util.List;

/* Mesmo formato de erro dos outros serviços. */
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
