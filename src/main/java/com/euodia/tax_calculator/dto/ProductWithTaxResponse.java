package com.euodia.tax_calculator.dto;

import com.euodia.tax_calculator.model.Country;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO pour la réponse avec calcul de taxes
 * Contient le produit original plus les informations de taxation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithTaxResponse {

    private Long id;
    private String name;
    private BigDecimal basePrice;
    private Country country;
    private BigDecimal taxAmount;
    private BigDecimal finalPrice;
    private String taxStrategyUsed;

}