package com.devluiz.productservice.services;

import com.devluiz.productservice.dto.CategoryDTO;
import com.devluiz.productservice.dto.ProductDTO;
import com.devluiz.productservice.dto.ProductMinDTO;
import com.devluiz.productservice.entities.Category;
import com.devluiz.productservice.entities.Product;
import com.devluiz.productservice.repositories.CategoryRepository;
import com.devluiz.productservice.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
        return new ProductDTO(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductMinDTO> findAll(String name, Pageable pageable) {
        return productRepository.searchByName(name, pageable).map(ProductMinDTO::new);
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product product = new Product();
        copyDtoToEntity(dto, product);
        product = productRepository.save(product);
        return new ProductDTO(product);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        // findById + orElseThrow: se o produto não existir, já devolve 404 aqui
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
        copyDtoToEntity(dto, product);
        product = productRepository.save(product);
        return new ProductDTO(product);
    }

    @Transactional
    public void delete(Long id) {
        // No monolito, apagar um id inexistente não dava erro nenhum.
        // Aqui a gente confere antes e devolve 404.
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produto não encontrado: " + id);
        }
        productRepository.deleteById(id);
    }

    private void copyDtoToEntity(ProductDTO dto, Product entity) {
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        entity.setPrice(dto.price());
        entity.setImgUrl(dto.imgUrl());

        entity.getCategories().clear();
        for (CategoryDTO catDto : dto.categories()) {
            Category category = categoryRepository.findById(catDto.id())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + catDto.id()));
            entity.getCategories().add(category);
        }
    }
}
