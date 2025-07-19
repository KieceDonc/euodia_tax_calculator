package com.euodia.tax_calculator.service;

import com.euodia.tax_calculator.model.Country;
import com.euodia.tax_calculator.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour ProductService
 */
@DisplayName("Product Service Tests")
class ProductServiceTest {

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService();
    }

    @Test
    @DisplayName("Should add product and generate ID")
    void testAddProduct() {
        // Given
        Product product = new Product("iPhone", new BigDecimal("999.99"), Country.US);

        // When
        Product savedProduct = productService.addProduct(product);

        // Then
        assertNotNull(savedProduct.getId());
        assertEquals("iPhone", savedProduct.getName());
        assertEquals(new BigDecimal("999.99"), savedProduct.getPrice());
        assertEquals(Country.US, savedProduct.getCountry());
    }

    @Test
    @DisplayName("Should generate unique IDs for multiple products")
    void testAddMultipleProducts() {
        // Given
        Product product1 = new Product("iPhone", new BigDecimal("999.99"), Country.US);
        Product product2 = new Product("Samsung", new BigDecimal("899.99"), Country.CANADA);

        // When
        Product saved1 = productService.addProduct(product1);
        Product saved2 = productService.addProduct(product2);

        // Then
        assertNotNull(saved1.getId());
        assertNotNull(saved2.getId());
        assertNotEquals(saved1.getId(), saved2.getId());
    }

    @Test
    @DisplayName("Should throw exception for null product")
    void testAddNullProduct() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.addProduct(null)
        );
        assertEquals("Product cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for invalid product")
    void testAddInvalidProduct() {
        // Given - produit avec nom null
        Product invalidProduct = new Product(null, new BigDecimal("100.00"), Country.US);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.addProduct(invalidProduct)
        );
        assertTrue(exception.getMessage().contains("Product is not valid"));
    }

    @Test
    @DisplayName("Should retrieve product by ID")
    void testGetProductById() {
        // Given
        Product product = new Product("iPhone", new BigDecimal("999.99"), Country.US);
        Product savedProduct = productService.addProduct(product);

        // When
        Optional<Product> retrievedProduct = productService.getProductById(savedProduct.getId());

        // Then
        assertTrue(retrievedProduct.isPresent());
        assertEquals(savedProduct.getId(), retrievedProduct.get().getId());
        assertEquals("iPhone", retrievedProduct.get().getName());
    }

    @Test
    @DisplayName("Should return empty for non-existent product")
    void testGetNonExistentProduct() {
        // When
        Optional<Product> product = productService.getProductById(999L);

        // Then
        assertTrue(product.isEmpty());
    }

    @Test
    @DisplayName("Should return empty for null ID")
    void testGetProductWithNullId() {
        // When
        Optional<Product> product = productService.getProductById(null);

        // Then
        assertTrue(product.isEmpty());
    }

    @Test
    @DisplayName("Should return all products")
    void testGetAllProducts() {
        // Given
        Product product1 = new Product("iPhone", new BigDecimal("999.99"), Country.US);
        Product product2 = new Product("Samsung", new BigDecimal("899.99"), Country.CANADA);

        productService.addProduct(product1);
        productService.addProduct(product2);

        // When
        var allProducts = productService.getAllProducts();

        // Then
        assertEquals(2, allProducts.size());
    }

    @Test
    @DisplayName("Should return defensive copy of all products")
    void testGetAllProductsDefensiveCopy() {
        // Given
        Product product = new Product("iPhone", new BigDecimal("999.99"), Country.US);
        productService.addProduct(product);

        // When
        var allProducts1 = productService.getAllProducts();
        var allProducts2 = productService.getAllProducts();

        // Then
        assertNotSame(allProducts1, allProducts2); // Différentes instances
        assertEquals(allProducts1.size(), allProducts2.size());
    }
}