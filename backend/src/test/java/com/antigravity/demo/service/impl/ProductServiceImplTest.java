package com.antigravity.demo.service.impl;

import com.antigravity.demo.dto.ProductDTOs.ProductCreateRequest;
import com.antigravity.demo.dto.ProductDTOs.ProductResponse;
import com.antigravity.demo.dto.ProductDTOs.ProductUpdateRequest;
import com.antigravity.demo.exception.ProductNotFoundException;
import com.antigravity.demo.model.Product;
import com.antigravity.demo.repository.ProductRepository;
import com.antigravity.demo.testsupport.AssertionsEx;
import com.antigravity.demo.testsupport.ProductTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createProduct_shouldCreateAndReturnResponse_WhenRequestValid() {
        // Arrange
        ProductCreateRequest request = ProductTestData.createRequest();
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ProductResponse response = productService.createProduct(request);

        // Assert
        AssertionsEx.assertUuid(response.id());
        AssertionsEx.assertNearNow(response.createdAt());
        assertEquals(request.name(), response.name());
        assertEquals(request.description(), response.description());
        assertEquals(request.price(), response.price());
        assertEquals(request.stock(), response.stock());

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(productCaptor.capture());

        Product capturedProduct = productCaptor.getValue();
        assertEquals(response.id(), capturedProduct.getId());
        AssertionsEx.assertNearNow(capturedProduct.getCreatedAt());
        assertEquals(request.name(), capturedProduct.getName());
    }

    @Test
    void getProduct_shouldReturnProduct_WhenExists() {
        // Arrange
        Product product = ProductTestData.createProduct();
        UUID id = product.getId();
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        // Act
        ProductResponse response = productService.getProduct(id);

        // Assert
        assertEquals(product.getId(), response.id());
        assertEquals(product.getName(), response.name());
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    void getProduct_shouldThrowProductNotFoundException_WhenMissing() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
                () -> productService.getProduct(id));
        assertTrue(exception.getMessage().contains(id.toString()));
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    void getAllProducts_shouldReturnMappedList_WhenProductsExist() {
        // Arrange
        Product p1 = ProductTestData.createProduct();
        Product p2 = ProductTestData.createProduct();
        when(productRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        // Act
        List<ProductResponse> responses = productService.getAllProducts();

        // Assert
        assertEquals(2, responses.size());
        assertEquals(p1.getId(), responses.get(0).id());
        assertEquals(p2.getId(), responses.get(1).id());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void updateProduct_shouldUpdateAndReturnResponse_WhenExists() {
        // Arrange
        Product existingProduct = ProductTestData.createProduct();
        UUID id = existingProduct.getId();
        ProductUpdateRequest request = ProductTestData.updateRequest();

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ProductResponse response = productService.updateProduct(id, request);

        // Assert
        assertEquals(id, response.id());
        assertEquals(request.name(), response.name());
        assertEquals(request.description(), response.description());
        assertEquals(request.price(), response.price());
        assertEquals(request.stock(), response.stock());

        verify(productRepository, times(1)).findById(id);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(productCaptor.capture());
        Product savedProduct = productCaptor.getValue();
        assertEquals(request.name(), savedProduct.getName());
    }

    @Test
    void updateProduct_shouldThrowProductNotFoundException_WhenMissing() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProductUpdateRequest request = ProductTestData.updateRequest();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(id, request));
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProduct_shouldDelete_WhenExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(productRepository.existsById(id)).thenReturn(true);

        // Act
        productService.deleteProduct(id);

        // Assert
        verify(productRepository, times(1)).existsById(id);
        verify(productRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteProduct_shouldThrowProductNotFoundException_WhenMissing() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(productRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(id));
        verify(productRepository, never()).deleteById(any());
    }
}
