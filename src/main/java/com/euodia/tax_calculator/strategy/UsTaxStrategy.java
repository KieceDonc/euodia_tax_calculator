package com.euodia.tax_calculator.strategy;

import com.euodia.tax_calculator.model.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Stratégie de calcul des taxes pour les États-Unis
 * Applique une taxe de vente de 8.5%
 */
@Component
public class UsTaxStrategy implements TaxStrategy {

    private static final BigDecimal US_TAX_RATE = new BigDecimal("0.085"); // 8.5%

    @Override
    public BigDecimal calculateTax(Product product) {
        if (product == null || product.getPrice() == null) {
            return BigDecimal.ZERO;
        }

        return product.getPrice()
                .multiply(US_TAX_RATE)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getStrategyName() {
        return "US Tax Strategy (8.5% sales tax)";
    }
}