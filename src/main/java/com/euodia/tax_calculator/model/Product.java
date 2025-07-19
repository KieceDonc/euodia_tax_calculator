package com.euodia.tax_calculator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Représente un produit avec ses propriétés de base
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private Long id;
    private String name;
    private BigDecimal price;
    private Country country;

    public Product(String name, BigDecimal price, Country country) {
        this.name = name;
        this.price = price;
        this.country = country;
    }

    public boolean isValid() {
        return name != null && !name.trim().isEmpty()
                && price != null && price.compareTo(BigDecimal.ZERO) >= 0
                && country != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}