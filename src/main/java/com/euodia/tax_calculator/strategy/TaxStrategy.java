package com.euodia.tax_calculator.strategy;

import com.euodia.tax_calculator.model.Product;

import java.math.BigDecimal;

/**
 * Interface définissant le contrat pour les stratégies de calcul de taxes.
 * Chaque pays aura sa propre implémentation de cette interface.
 */
public interface TaxStrategy {

    BigDecimal calculateTax(Product product);

    /**
     * Calcule le prix final incluant les taxes
     *
     * @param product le produit pour lequel calculer le prix final
     * @return le prix du produit + taxes
     */
    default BigDecimal calculateFinalPrice(Product product) {
        BigDecimal tax = calculateTax(product);
        return product.getPrice().add(tax);
    }

    String getStrategyName();
}