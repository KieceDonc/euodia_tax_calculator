package com.euodia.tax_calculator.strategy;

import com.euodia.tax_calculator.model.Country;
import com.euodia.tax_calculator.model.Product;
import com.euodia.tax_calculator.service.TaxCalculationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour toutes les stratégies de taxation
 */
@DisplayName("Tax Strategy Tests")
class TaxStrategyTest {

    private UsTaxStrategy usTaxStrategy;
    private CanadaTaxStrategy canadaTaxStrategy;
    private FranceTaxStrategy franceTaxStrategy;

    @BeforeEach
    void setUp() {
        usTaxStrategy = new UsTaxStrategy();
        canadaTaxStrategy = new CanadaTaxStrategy();
        franceTaxStrategy = new FranceTaxStrategy();
    }

    @Test
    @DisplayName("US Tax Strategy - Should calculate 8.5% tax correctly")
    void testUsTaxStrategy() {
        // Given
        Product product = new Product(1L, "Test Product", new BigDecimal("100.00"), Country.US);

        // When
        BigDecimal tax = usTaxStrategy.calculateTax(product);
        BigDecimal finalPrice = usTaxStrategy.calculateFinalPrice(product);

        // Then
        assertEquals(new BigDecimal("8.50"), tax);
        assertEquals(new BigDecimal("108.50"), finalPrice);
        assertEquals("US Tax Strategy (8.5% sales tax)", usTaxStrategy.getStrategyName());
    }

    @Test
    @DisplayName("Canada Tax Strategy - Should calculate 12% tax correctly (5% GST + 7% PST)")
    void testCanadaTaxStrategy() {
        // Given
        Product product = new Product(1L, "Test Product", new BigDecimal("100.00"), Country.CANADA);

        // When
        BigDecimal tax = canadaTaxStrategy.calculateTax(product);
        BigDecimal finalPrice = canadaTaxStrategy.calculateFinalPrice(product);

        // Then
        assertEquals(new BigDecimal("12.00"), tax);
        assertEquals(new BigDecimal("112.00"), finalPrice);
        assertEquals("Canada Tax Strategy (5% GST + 7% PST = 12% total)", canadaTaxStrategy.getStrategyName());
    }

    @Test
    @DisplayName("France Tax Strategy - Should calculate 20% VAT correctly")
    void testFranceTaxStrategy() {
        // Given
        Product product = new Product(1L, "Test Product", new BigDecimal("100.00"), Country.FRANCE);

        // When
        BigDecimal tax = franceTaxStrategy.calculateTax(product);
        BigDecimal finalPrice = franceTaxStrategy.calculateFinalPrice(product);

        // Then
        assertEquals(new BigDecimal("20.00"), tax);
        assertEquals(new BigDecimal("120.00"), finalPrice);
        assertEquals("France Tax Strategy (20% TVA)", franceTaxStrategy.getStrategyName());
    }

    @Test
    @DisplayName("All strategies - Should handle null product gracefully")
    void testNullProduct() {
        // When & Then
        assertEquals(BigDecimal.ZERO, usTaxStrategy.calculateTax(null));
        assertEquals(BigDecimal.ZERO, canadaTaxStrategy.calculateTax(null));
        assertEquals(BigDecimal.ZERO, franceTaxStrategy.calculateTax(null));
    }

    @Test
    @DisplayName("All strategies - Should handle null price gracefully")
    void testNullPrice() {
        // Given
        Product productWithNullPrice = new Product(1L, "Test", null, Country.US);

        // When & Then
        assertEquals(BigDecimal.ZERO, usTaxStrategy.calculateTax(productWithNullPrice));
        assertEquals(BigDecimal.ZERO, canadaTaxStrategy.calculateTax(productWithNullPrice));
        assertEquals(BigDecimal.ZERO, franceTaxStrategy.calculateTax(productWithNullPrice));
    }

    @Test
    @DisplayName("Tax calculation - Should handle decimal precision correctly")
    void testDecimalPrecision() {
        // Given
        Product product = new Product(1L, "Test Product", new BigDecimal("99.99"), Country.US);

        // When
        BigDecimal tax = usTaxStrategy.calculateTax(product);

        // Then
        assertEquals(new BigDecimal("8.50"), tax); // 99.99 * 0.085 = 8.4991 → arrondi à 8.50
        assertEquals(2, tax.scale()); // Vérifie que nous avons 2 décimales
    }

    @Test
    @DisplayName("Tax calculation - Should work with zero price")
    void testZeroPrice() {
        // Given
        Product product = new Product(1L, "Free Product", BigDecimal.ZERO, Country.FRANCE);

        // When
        BigDecimal tax = franceTaxStrategy.calculateTax(product);
        BigDecimal finalPrice = franceTaxStrategy.calculateFinalPrice(product);

        // Then
        assertEquals(0, tax.compareTo(BigDecimal.ZERO));
        assertEquals(0, finalPrice.compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Tax calculation - Should work with large amounts")
    void testLargeAmount() {
        // Given
        Product expensiveProduct = new Product(1L, "Expensive Item", new BigDecimal("10000.00"), Country.CANADA);

        // When
        BigDecimal tax = canadaTaxStrategy.calculateTax(expensiveProduct);
        BigDecimal finalPrice = canadaTaxStrategy.calculateFinalPrice(expensiveProduct);

        // Then
        assertEquals(new BigDecimal("1200.00"), tax); // 10000 * 0.12
        assertEquals(new BigDecimal("11200.00"), finalPrice);
    }
}