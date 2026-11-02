package com.devluiz.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/*
 * O que o cliente ENVIA para criar um pedido: só o id do produto e a quantidade.
 * O preço NÃO vem do cliente (senão ele mandaria "price": 0.01).
 * Quem diz o preço é o product-service.
 */
public record OrderRequest(
        @NotEmpty(message = "O pedido deve ter pelo menos um item.")
        List<@Valid Item> items
) {

    public record Item(
            @NotNull(message = "Campo requerido")
            Long productId,

            @NotNull(message = "Campo requerido")
            @Positive(message = "A quantidade deve ser positiva")
            Integer quantity
    ) {
    }
}
