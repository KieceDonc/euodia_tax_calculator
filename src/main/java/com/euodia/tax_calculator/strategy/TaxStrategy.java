package com.euodia.tax_calculator.strategy;

import com.euodia.tax_calculator.model.Product;

import java.math.BigDecimal;

/**
 * Interface définissant le contrat pour les stratégies de calcul de taxes.
 * Chaque pays aura sa propre implémentation de cette interface.
 */
public interface TaxStrategy {

    BigDecimal calculateTax(Product product);

    String getStrategyName();
}