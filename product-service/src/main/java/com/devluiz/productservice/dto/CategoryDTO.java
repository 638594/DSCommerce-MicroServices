package com.devluiz.productservice.dto;

import com.devluiz.productservice.entities.Category;

public record CategoryDTO(Long id, String name) {

    public CategoryDTO(Category entity) {
        this(entity.getId(), entity.getName());
    }
}
