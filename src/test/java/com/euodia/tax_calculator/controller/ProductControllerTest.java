package com.euodia.tax_calculator.controller;

import com.euodia.tax_calculator.dto.CreateProductRequest;
import com.euodia.tax_calculator.dto.ProductWithTaxResponse;
import com.euodia.tax_calculator.model.Country;
import com.euodia.tax_calculator.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour ProductController
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Product Controller Tests")
class ProductControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/products";
    }

    @Test
    @DisplayName("POST /api/products - Should create product successfully")
    void testCreateProduct() {
        // Given
        CreateProductRequest request = new CreateProductRequest(
                "iPhone 15",
                new BigDecimal("999.99"),
                Country.US
        );

        // When
        ResponseEntity<Product> response = restTemplate.postForEntity(
                getBaseUrl(),
                request,
                Product.class
        );

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        Product createdProduct = response.getBody();
        assertNotNull(createdProduct.getId());
        assertEquals("iPhone 15", createdProduct.getName());
        assertEquals(new BigDecimal("999.99"), createdProduct.getPrice());
        assertEquals(Country.US, createdProduct.getCountry());
    }

    @Test
    @DisplayName("POST /api/products - Should return 400 for invalid product")
    void testCreateInvalidProduct() {
        // Given - produit avec nom vide
        CreateProductRequest request = new CreateProductRequest(
                "",
                new BigDecimal("999.99"),
                Country.US
        );

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl(),
                request,
                String.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("POST /api/products - Should return 400 for negative price")
    void testCreateProductWithNegativePrice() {
        // Given
        CreateProductRequest request = new CreateProductRequest(
                "Test Product",
                new BigDecimal("-10.00"),
                Country.US
        );

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl(),
                request,
                String.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should retrieve product by ID")
    void testGetProductById() {
        // Given - créer d'abord un produit
        CreateProductRequest request = new CreateProductRequest(
                "Samsung Galaxy",
                new BigDecimal("899.99"),
                Country.CANADA
        );

        ResponseEntity<Product> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                request,
                Product.class
        );

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        Long productId = createResponse.getBody().getId();

        // When
        ResponseEntity<Product> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + productId,
                Product.class
        );

        // Then
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());

        Product retrievedProduct = getResponse.getBody();
        assertEquals(productId, retrievedProduct.getId());
        assertEquals("Samsung Galaxy", retrievedProduct.getName());
        assertEquals(new BigDecimal("899.99"), retrievedProduct.getPrice());
        assertEquals(Country.CANADA, retrievedProduct.getCountry());
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return 404 for non-existent product")
    void testGetNonExistentProduct() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/999",
                String.class
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /api/products/{id}/with-tax - Should calculate tax for US product")
    void testCalculateTaxForUSProduct() {
        // Given - créer un produit US
        CreateProductRequest request = new CreateProductRequest(
                "US Product",
                new BigDecimal("100.00"),
                Country.US
        );

        ResponseEntity<Product> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                request,
                Product.class
        );

        Long productId = createResponse.getBody().getId();

        // When
        ResponseEntity<ProductWithTaxResponse> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + productId + "/with-tax",
                ProductWithTaxResponse.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ProductWithTaxResponse taxResponse = response.getBody();
        assertEquals(productId, taxResponse.getId());
        assertEquals("US Product", taxResponse.getName());
        assertEquals(new BigDecimal("100.00"), taxResponse.getBasePrice());
        assertEquals(Country.US, taxResponse.getCountry());
        assertEquals(new BigDecimal("8.50"), taxResponse.getTaxAmount()); // 8.5% de 100
        assertEquals(new BigDecimal("108.50"), taxResponse.getFinalPrice());
        assertTrue(taxResponse.getTaxStrategyUsed().contains("US Tax Strategy"));
    }

    @Test
    @DisplayName("GET /api/products/{id}/with-tax - Should calculate tax for Canada product")
    void testCalculateTaxForCanadaProduct() {
        // Given
        CreateProductRequest request = new CreateProductRequest(
                "Canada Product",
                new BigDecimal("100.00"),
                Country.CANADA
        );

        ResponseEntity<Product> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                request,
                Product.class
        );

        Long productId = createResponse.getBody().getId();

        // When
        ResponseEntity<ProductWithTaxResponse> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + productId + "/with-tax",
                ProductWithTaxResponse.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ProductWithTaxResponse taxResponse = response.getBody();
        assertEquals(new BigDecimal("12.00"), taxResponse.getTaxAmount()); // 12% de 100
        assertEquals(new BigDecimal("112.00"), taxResponse.getFinalPrice());
        assertTrue(taxResponse.getTaxStrategyUsed().contains("Canada Tax Strategy"));
    }

    @Test
    @DisplayName("GET /api/products/{id}/with-tax - Should calculate tax for France product")
    void testCalculateTaxForFranceProduct() {
        // Given
        CreateProductRequest request = new CreateProductRequest(
                "France Product",
                new BigDecimal("100.00"),
                Country.FRANCE
        );

        ResponseEntity<Product> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                request,
                Product.class
        );

        Long productId = createResponse.getBody().getId();

        // When
        ResponseEntity<ProductWithTaxResponse> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + productId + "/with-tax",
                ProductWithTaxResponse.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ProductWithTaxResponse taxResponse = response.getBody();
        assertEquals(new BigDecimal("20.00"), taxResponse.getTaxAmount()); // 20% de 100
        assertEquals(new BigDecimal("120.00"), taxResponse.getFinalPrice());
        assertTrue(taxResponse.getTaxStrategyUsed().contains("France Tax Strategy"));
    }

    @Test
    @DisplayName("GET /api/products/{id}/with-tax - Should return 404 for non-existent product")
    void testCalculateTaxForNonExistentProduct() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/999/with-tax",
                String.class
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /api/products - Should return all products")
    void testGetAllProducts() {
        // Given - créer plusieurs produits
        CreateProductRequest request1 = new CreateProductRequest("Product 1",
                new BigDecimal("100.00"), Country.US);
        CreateProductRequest request2 = new CreateProductRequest("Product 2",
                new BigDecimal("200.00"), Country.CANADA);

        restTemplate.postForEntity(getBaseUrl(), request1, Product.class);
        restTemplate.postForEntity(getBaseUrl(), request2, Product.class);

        // When
        ResponseEntity<Map> response = restTemplate.getForEntity(
                getBaseUrl(),
                Map.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> allProducts = response.getBody();
        assertEquals(2, allProducts.size());
    }

    @Test
    @DisplayName("Integration test - Complete product lifecycle")
    void testCompleteProductLifecycle() {
        // Given
        CreateProductRequest request = new CreateProductRequest(
                "Integration Test Product",
                new BigDecimal("150.75"),
                Country.FRANCE
        );

        // Create product
        ResponseEntity<Product> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                request,
                Product.class
        );

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        Product createdProduct = createResponse.getBody();
        assertEquals("Integration Test Product", createdProduct.getName());
        Long productId = createdProduct.getId();

        // Retrieve product
        ResponseEntity<Product> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + productId,
                Product.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("Integration Test Product", getResponse.getBody().getName());

        // Calculate tax (France 20% VAT)
        ResponseEntity<ProductWithTaxResponse> taxResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + productId + "/with-tax",
                ProductWithTaxResponse.class
        );

        assertEquals(HttpStatus.OK, taxResponse.getStatusCode());

        ProductWithTaxResponse taxResult = taxResponse.getBody();
        assertEquals(new BigDecimal("150.75"), taxResult.getBasePrice());
        assertEquals(new BigDecimal("30.15"), taxResult.getTaxAmount()); // 150.75 * 0.20 = 30.15
        assertEquals(new BigDecimal("180.90"), taxResult.getFinalPrice()); // 150.75 + 30.15 = 180.90
        assertTrue(taxResult.getTaxStrategyUsed().contains("France Tax Strategy"));
    }

    @Test
    @DisplayName("Should handle concurrent product creation")
    void testConcurrentProductCreation() {
        // Given
        CreateProductRequest request1 = new CreateProductRequest("Concurrent Product 1",
                new BigDecimal("50.00"), Country.US);
        CreateProductRequest request2 = new CreateProductRequest("Concurrent Product 2",
                new BigDecimal("75.00"), Country.CANADA);

        // When - créer simultanément
        ResponseEntity<Product> response1 = restTemplate.postForEntity(getBaseUrl(), request1, Product.class);
        ResponseEntity<Product> response2 = restTemplate.postForEntity(getBaseUrl(), request2, Product.class);

        // Then
        assertEquals(HttpStatus.CREATED, response1.getStatusCode());
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());

        // Vérifier que les IDs sont uniques
        assertNotEquals(response1.getBody().getId(), response2.getBody().getId());

        // Vérifier que les deux produits sont accessibles
        ResponseEntity<Map> allProductsResponse = restTemplate.getForEntity(getBaseUrl(), Map.class);
        assertEquals(2, allProductsResponse.getBody().size());
    }
}