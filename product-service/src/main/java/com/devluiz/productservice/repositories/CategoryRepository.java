package com.devluiz.productservice.repositories;

import com.devluiz.productservice.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
