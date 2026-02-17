package com.antigravity.demo.testsupport;

import com.antigravity.demo.dto.ProductDTOs.ProductCreateRequest;
import com.antigravity.demo.dto.ProductDTOs.ProductUpdateRequest;
import com.antigravity.demo.model.Product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Data factory for Product-related objects to be used in unit tests.
 */
public class ProductTestData {

    public static final String DEFAULT_NAME = "Test Product";
    public static final String DEFAULT_DESC = "Test Description";
    public static final BigDecimal DEFAULT_PRICE = new BigDecimal("99.99");
    public static final Integer DEFAULT_STOCK = 10;

    public static ProductCreateRequest createRequest() {
        return new ProductCreateRequest(DEFAULT_NAME, DEFAULT_DESC, DEFAULT_PRICE, DEFAULT_STOCK);
    }

    public static ProductUpdateRequest updateRequest() {
        return new ProductUpdateRequest("Updated Name", "Updated Desc", new BigDecimal("149.99"), 20);
    }

    public static Product createProduct() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(DEFAULT_NAME);
        product.setDescription(DEFAULT_DESC);
        product.setPrice(DEFAULT_PRICE);
        product.setStock(DEFAULT_STOCK);
        product.setCreatedAt(Instant.now());
        return product;
    }
}
