package com.euodia.tax_calculator.strategy;

import com.euodia.tax_calculator.model.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Stratégie de calcul des taxes pour la France
 * Applique la TVA standard de 20%
 */
@Component
public class FranceTaxStrategy implements TaxStrategy {

    private static final BigDecimal TVA_RATE = new BigDecimal("0.20"); // 20% TVA

    @Override
    public BigDecimal calculateTax(Product product) {
        if (product == null || product.getPrice() == null) {
            return BigDecimal.ZERO;
        }

        return product.getPrice()
                .multiply(TVA_RATE)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getStrategyName() {
        return "France Tax Strategy (20% TVA)";
    }
}