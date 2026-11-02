package com.devluiz.productservice.dto;

import com.devluiz.productservice.entities.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

public record ProductDTO(
        Long id,

        @NotBlank(message = "Campo requerido")
        @Size(min = 3, max = 80, message = "Nome precisa ter entre 3 e 80 caracteres")
        String name,

        @NotBlank(message = "Campo requerido")
        @Size(min = 10, message = "Descricao precisa ter no minimo 10 caracteres")
        String description,

        @NotNull(message = "Campo requerido")
        @Positive(message = "Valor do preço deve ser positivo")
        BigDecimal price,

        String imgUrl,

        @NotEmpty(message = "Deve ter pelo menos uma categoria.")
        Set<CategoryDTO> categories
) {

    public ProductDTO(Product entity) {
        this(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getImgUrl(),
                entity.getCategories().stream()
                        .map(CategoryDTO::new)
                        .collect(Collectors.toSet())
        );
    }
}
