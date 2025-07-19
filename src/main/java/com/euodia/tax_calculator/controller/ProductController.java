package com.euodia.tax_calculator.controller;

import com.euodia.tax_calculator.dto.CreateProductRequest;
import com.euodia.tax_calculator.dto.ProductWithTaxResponse;
import com.euodia.tax_calculator.model.Product;
import com.euodia.tax_calculator.service.ProductService;
import com.euodia.tax_calculator.service.TaxCalculationService;
import com.euodia.tax_calculator.strategy.TaxStrategy;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des produits et calculs de taxes
 * Expose les endpoints demandés dans les spécifications
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final TaxCalculationService taxCalculationService;

    /**
     * POST /api/products
     * Ajoute un nouveau produit
     *
     * @param request les données du produit à créer
     * @return le produit créé avec son ID
     */
    @PostMapping
    public ResponseEntity<Product> addProduct(@Valid @RequestBody CreateProductRequest request) {
        log.info("Received request to create product: {}", request.getName());

        try {
            // Conversion DTO -> Entity
            // Dans le cadre de plus gros projet, on utiliserait mapstruct ...
            Product product = new Product(request.getName(), request.getPrice(), request.getCountry());

            // Sauvegarde
            Product savedProduct = productService.addProduct(product);

            log.info("Product created successfully with ID: {}", savedProduct.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid product data: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error creating product", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/products/{id}
     * Récupère les détails d'un produit par son ID
     *
     * @param id l'ID du produit à récupérer
     * @return les détails du produit
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        log.info("Received request to get product with ID: {}", id);

        Optional<Product> product = productService.getProductById(id);

        if (product.isPresent()) {
            log.info("Product found: {}", product.get().getName());
            return ResponseEntity.ok(product.get());
        } else {
            log.info("Product not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/products/{id}/with-tax
     * Calcule et retourne le prix final d'un produit incluant les taxes
     *
     * @param id l'ID du produit
     * @return les détails du produit avec calcul des taxes
     */
    @GetMapping("/{id}/with-tax")
    public ResponseEntity<ProductWithTaxResponse> getProductWithTax(@PathVariable Long id) {
        log.info("Received request to calculate tax for product ID: {}", id);

        Optional<Product> productOpt = productService.getProductById(id);

        if (productOpt.isEmpty()) {
            log.info("Product not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }

        Product product = productOpt.get();

        try {
            // Calcul des taxes
            BigDecimal taxAmount = taxCalculationService.calculateTax(product);
            BigDecimal finalPrice = taxCalculationService.calculateFinalPrice(product);

            // Récupération du nom de la stratégie utilisée
            TaxStrategy strategy = taxCalculationService.getStrategyForCountry(product.getCountry());
            String strategyName = strategy != null ? strategy.getStrategyName() : "Unknown strategy";

            // Construction de la réponse
            ProductWithTaxResponse response = new ProductWithTaxResponse(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getCountry(),
                    taxAmount,
                    finalPrice,
                    strategyName
            );

            log.info("Tax calculation completed for product {}: base={}, tax={}, final={}",
                    product.getName(), product.getPrice(), taxAmount, finalPrice);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Error calculating tax for product {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error calculating tax for product " + id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/products
     * Endpoint bonus pour lister tous les produits
     *
     * @return la liste de tous les produits
     */
    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        log.info("Received request to get all products");

        try {
            var allProducts = productService.getAllProducts();
            log.info("Returning {} products", allProducts.size());
            return ResponseEntity.ok(allProducts);
        } catch (Exception e) {
            log.error("Error getting all products", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}