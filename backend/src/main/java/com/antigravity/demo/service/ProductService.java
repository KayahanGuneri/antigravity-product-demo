package com.antigravity.demo.service;

import com.antigravity.demo.dto.ProductDTOs.*;
import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse createProduct(ProductCreateRequest request);

    ProductResponse getProduct(UUID id);

    List<ProductResponse> getAllProducts();

    ProductResponse updateProduct(UUID id, ProductUpdateRequest request);

    void deleteProduct(UUID id);
}
