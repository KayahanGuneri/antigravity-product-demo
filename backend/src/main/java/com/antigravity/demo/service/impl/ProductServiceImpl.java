package com.antigravity.demo.service.impl;

import com.antigravity.demo.dto.ProductDTOs.*;
import com.antigravity.demo.exception.ProductNotFoundException;
import com.antigravity.demo.model.Product;
import com.antigravity.demo.repository.ProductRepository;
import com.antigravity.demo.service.InputSanitizer;
import com.antigravity.demo.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponse createProduct(ProductCreateRequest request) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(InputSanitizer.sanitize(request.name(), "Name", 100, true));
        product.setDescription(InputSanitizer.sanitize(request.description(), "Description", 1000, false));
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCreatedAt(Instant.now());

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(UUID id) {
        return productRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse updateProduct(UUID id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        product.setName(InputSanitizer.sanitize(request.name(), "Name", 100, true));
        product.setDescription(InputSanitizer.sanitize(request.description(), "Description", 1000, false));
        product.setPrice(request.price());
        product.setStock(request.stock());

        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCreatedAt());
    }
}
