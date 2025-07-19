package com.euodia.tax_calculator.service;

import com.euodia.tax_calculator.model.Country;
import com.euodia.tax_calculator.model.Product;
import com.euodia.tax_calculator.strategy.CanadaTaxStrategy;
import com.euodia.tax_calculator.strategy.FranceTaxStrategy;
import com.euodia.tax_calculator.strategy.TaxStrategy;
import com.euodia.tax_calculator.strategy.UsTaxStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour TaxCalculationService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tax Calculation Service Tests")
class TaxCalculationServiceTest {

    @Mock
    private UsTaxStrategy usTaxStrategy;

    @Mock
    private CanadaTaxStrategy canadaTaxStrategy;

    @Mock
    private FranceTaxStrategy franceTaxStrategy;

    @InjectMocks
    private TaxCalculationService taxCalculationService;

    private Product usProduct;
    private Product canadaProduct;
    private Product franceProduct;

    @BeforeEach
    void setUp() {
        usProduct = new Product(1L, "US Product", new BigDecimal("100.00"), Country.US);
        canadaProduct = new Product(2L, "Canada Product", new BigDecimal("100.00"), Country.CANADA);
        franceProduct = new Product(3L, "France Product", new BigDecimal("100.00"), Country.FRANCE);
    }

    @Test
    @DisplayName("Should select correct strategy for US products")
    void testCalculateTaxForUSProduct() {
        // Given
        BigDecimal expectedTax = new BigDecimal("8.50");
        when(usTaxStrategy.calculateTax(usProduct)).thenReturn(expectedTax);

        // When
        BigDecimal actualTax = taxCalculationService.calculateTax(usProduct);

        // Then
        assertEquals(expectedTax, actualTax);
        verify(usTaxStrategy).calculateTax(usProduct);
        verifyNoInteractions(canadaTaxStrategy, franceTaxStrategy);
    }

    @Test
    @DisplayName("Should select correct strategy for Canada products")
    void testCalculateTaxForCanadaProduct() {
        // Given
        BigDecimal expectedTax = new BigDecimal("12.00");
        when(canadaTaxStrategy.calculateTax(canadaProduct)).thenReturn(expectedTax);

        // When
        BigDecimal actualTax = taxCalculationService.calculateTax(canadaProduct);

        // Then
        assertEquals(expectedTax, actualTax);
        verify(canadaTaxStrategy).calculateTax(canadaProduct);
        verifyNoInteractions(usTaxStrategy, franceTaxStrategy);
    }

    @Test
    @DisplayName("Should select correct strategy for France products")
    void testCalculateTaxForFranceProduct() {
        // Given
        BigDecimal expectedTax = new BigDecimal("20.00");
        when(franceTaxStrategy.calculateTax(franceProduct)).thenReturn(expectedTax);

        // When
        BigDecimal actualTax = taxCalculationService.calculateTax(franceProduct);

        // Then
        assertEquals(expectedTax, actualTax);
        verify(franceTaxStrategy).calculateTax(franceProduct);
        verifyNoInteractions(usTaxStrategy, canadaTaxStrategy);
    }

    @Test
    @DisplayName("Should calculate final price correctly")
    void testCalculateFinalPrice() {
        // Given
        BigDecimal expectedTax = new BigDecimal("8.50");
        BigDecimal expectedFinalPrice = new BigDecimal("108.50");
        when(usTaxStrategy.calculateTax(usProduct)).thenReturn(expectedTax);

        // When
        BigDecimal actualFinalPrice = taxCalculationService.calculateFinalPrice(usProduct);

        // Then
        assertEquals(expectedFinalPrice, actualFinalPrice);
        verify(usTaxStrategy).calculateTax(usProduct);
    }

    @Test
    @DisplayName("Should return zero tax for null product")
    void testCalculateTaxForNullProduct() {
        // When
        BigDecimal tax = taxCalculationService.calculateTax(null);

        // Then
        assertEquals(BigDecimal.ZERO, tax);
        verifyNoInteractions(usTaxStrategy, canadaTaxStrategy, franceTaxStrategy);
    }

    @Test
    @DisplayName("Should return zero tax for product with null country")
    void testCalculateTaxForProductWithNullCountry() {
        // Given
        Product productWithNullCountry = new Product(1L, "Test", new BigDecimal("100.00"), null);

        // When
        BigDecimal tax = taxCalculationService.calculateTax(productWithNullCountry);

        // Then
        assertEquals(BigDecimal.ZERO, tax);
        verifyNoInteractions(usTaxStrategy, canadaTaxStrategy, franceTaxStrategy);
    }

    @Test
    @DisplayName("Should return zero final price for null product")
    void testCalculateFinalPriceForNullProduct() {
        // When
        BigDecimal finalPrice = taxCalculationService.calculateFinalPrice(null);

        // Then
        assertEquals(BigDecimal.ZERO, finalPrice);
        verifyNoInteractions(usTaxStrategy, canadaTaxStrategy, franceTaxStrategy);
    }

    @Test
    @DisplayName("Should return zero final price for product with null price")
    void testCalculateFinalPriceForProductWithNullPrice() {
        // Given
        Product productWithNullPrice = new Product(1L, "Test", null, Country.US);

        // When
        BigDecimal finalPrice = taxCalculationService.calculateFinalPrice(productWithNullPrice);

        // Then
        assertEquals(BigDecimal.ZERO, finalPrice);
        verifyNoInteractions(usTaxStrategy, canadaTaxStrategy, franceTaxStrategy);
    }

    @Test
    @DisplayName("Should return correct strategy for each country")
    void testGetStrategyForCountry() {
        // When & Then
        TaxStrategy usStrategy = taxCalculationService.getStrategyForCountry(Country.US);
        TaxStrategy canadaStrategy = taxCalculationService.getStrategyForCountry(Country.CANADA);
        TaxStrategy franceStrategy = taxCalculationService.getStrategyForCountry(Country.FRANCE);

        assertSame(usTaxStrategy, usStrategy);
        assertSame(canadaTaxStrategy, canadaStrategy);
        assertSame(franceTaxStrategy, franceStrategy);
    }
}