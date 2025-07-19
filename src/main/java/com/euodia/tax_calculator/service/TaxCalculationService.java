package com.euodia.tax_calculator.service;

import com.euodia.tax_calculator.model.Country;
import com.euodia.tax_calculator.model.Product;
import com.euodia.tax_calculator.strategy.CanadaTaxStrategy;
import com.euodia.tax_calculator.strategy.FranceTaxStrategy;
import com.euodia.tax_calculator.strategy.TaxStrategy;
import com.euodia.tax_calculator.strategy.UsTaxStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Service responsable de la sélection et de l'exécution des stratégies de taxation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaxCalculationService {

    private final UsTaxStrategy usTaxStrategy;
    private final CanadaTaxStrategy canadaTaxStrategy;
    private final FranceTaxStrategy franceTaxStrategy;

    // Map pour associer chaque pays à sa stratégie correspondante
    private Map<Country, TaxStrategy> getStrategyMap() {
        return Map.of(
                Country.US, usTaxStrategy,
                Country.CANADA, canadaTaxStrategy,
                Country.FRANCE, franceTaxStrategy
        );
    }

    /**
     * Sélectionne la stratégie appropriée et calcule les taxes
     *
     * @param product le produit pour lequel calculer les taxes
     * @return le montant des taxes
     * @throws IllegalArgumentException si le pays n'est pas supporté
     */
    public BigDecimal calculateTax(Product product) {
        if (product == null || product.getCountry() == null) {
            log.warn("Product or country is null, returning zero tax");
            return BigDecimal.ZERO;
        }

        TaxStrategy strategy = getStrategyMap().get(product.getCountry());

        if (strategy == null) {
            throw new IllegalArgumentException("No tax strategy found for country: " + product.getCountry());
        }

        BigDecimal tax = strategy.calculateTax(product);
        log.debug("Calculated tax for product {} in {}: {} using strategy: {}",
                product.getName(), product.getCountry(), tax, strategy.getStrategyName());

        return tax;
    }

    /**
     * Calcule le prix final (prix + taxes) pour un produit
     *
     * @param product le produit pour lequel calculer le prix final
     * @return le prix final incluant les taxes
     */
    public BigDecimal calculateFinalPrice(Product product) {
        if (product == null || product.getPrice() == null) {
            log.warn("Product or price is null, returning zero");
            return BigDecimal.ZERO;
        }

        BigDecimal tax = calculateTax(product);
        BigDecimal finalPrice = product.getPrice().add(tax);

        log.info("Final price calculation for product {}: base price {} + tax {} = final price {}",
                product.getName(), product.getPrice(), tax, finalPrice);

        return finalPrice;
    }

    /**
     * Retourne la stratégie utilisée pour un pays donné (utile pour les tests et le debugging)
     *
     * @param country le pays pour lequel récupérer la stratégie
     * @return la stratégie correspondante ou null si non trouvée
     */
    public TaxStrategy getStrategyForCountry(Country country) {
        return getStrategyMap().get(country);
    }
}
