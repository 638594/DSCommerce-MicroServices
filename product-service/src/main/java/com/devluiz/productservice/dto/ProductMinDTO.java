package com.devluiz.productservice.dto;

import com.devluiz.productservice.entities.Product;

import java.math.BigDecimal;

public record ProductMinDTO(Long id, String name, BigDecimal price, String imgUrl) {

    public ProductMinDTO(Product entity) {
        this(entity.getId(), entity.getName(), entity.getPrice(), entity.getImgUrl());
    }
}
