package com.euodia.tax_calculator.strategy;

import com.euodia.tax_calculator.model.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Stratégie de calcul des taxes pour le Canada
 * Applique GST (5%) + PST provincial = 12% total
 */
@Component
public class CanadaTaxStrategy implements TaxStrategy {

    private static final BigDecimal GST_RATE = new BigDecimal("0.05");  // 5% GST
    private static final BigDecimal PST_RATE = new BigDecimal("0.07");  // 7% PST

    @Override
    public BigDecimal calculateTax(Product product) {
        if (product == null || product.getPrice() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal price = product.getPrice();

        // Calcul GST
        BigDecimal gst = price.multiply(GST_RATE);

        // Calcul PST
        BigDecimal pst = price.multiply(PST_RATE);

        // Total des taxes
        return gst.add(pst).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getStrategyName() {
        return "Canada Tax Strategy (5% GST + 7% PST = 12% total)";
    }
}