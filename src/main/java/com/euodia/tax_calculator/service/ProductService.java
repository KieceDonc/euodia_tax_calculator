package com.euodia.tax_calculator.service;

import com.euodia.tax_calculator.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service de gestion des produits.
 * Utilise une Map en mémoire pour simuler une base de données.
 * Dans un projet réel, une base de données relationnelle (via JPA/Hibernate) serait utilisée.
 * L'utilisation d'une base embarquée comme H2 est possible,
 * mais rajoute inutilement une complexité pour ce test.
 */
@Service
@Slf4j
public class ProductService {

    // Simulation d'une base de données en mémoire
    private final Map<Long, Product> products = new HashMap<>();

    // Générateur d'ID automatique
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Ajoute un nouveau produit
     *
     * @param product le produit à ajouter (sans ID)
     * @return le produit sauvegardé avec son ID généré
     * @throws IllegalArgumentException si le produit n'est pas valide
     */
    public Product addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        if (!product.isValid()) {
            throw new IllegalArgumentException("Product is not valid: " + product);
        }

        // Génération d'un nouvel ID
        Long newId = idGenerator.getAndIncrement();
        product.setId(newId);

        // Sauvegarde
        products.put(newId, product);

        log.info("Product added with ID {}: {}", newId, product.getName());
        return product;
    }

    /**
     * Récupère un produit par son ID
     *
     * @param id l'ID du produit à récupérer
     * @return Optional contenant le produit si trouvé, sinon Optional.empty()
     */
    public Optional<Product> getProductById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        Product product = products.get(id);
        log.debug("Product lookup for ID {}: {}", id, product != null ? "found" : "not found");
        return Optional.ofNullable(product);
    }

    /**
     * Récupère tous les produits (utile pour les tests ou future extension)
     *
     * @return Map de tous les produits
     */
    public Map<Long, Product> getAllProducts() {
        return new HashMap<>(products); // Copie défensive
    }
}