# Tax Calculator

Application Spring Boot implémentant le pattern Stratégie pour calculer les taxes sur des produits selon le pays de vente.

## Prérequis

- Java 21 ou supérieur
- Maven 3.5.3 ou supérieur

## Installation et exécution

### 1. Cloner le projet
```bash
git clone git@github.com:KieceDonc/euodia_tax_calculator.git
cd euodia_tax_calculator
```

### 2. Compiler le projet
```bash
./mvnw clean compile
```

### 3. Exécuter les tests
```bash
# Tous les tests
./mvnw test

# Tests spécifiques
./mvnw test -Dtest=TaxStrategyTest
```

### 4. Lancer l'application
```bash
./mvnw spring-boot:run
```

L'application sera accessible sur `http://localhost:8080`

## API Endpoints

### Créer un produit
```bash
POST /api/products
Content-Type: application/json

{
  "name": "iPhone 15",
  "price": 999.99,
  "country": "US"
}
```

### Récupérer un produit
```bash
GET /api/products/{id}
```

### Calculer le prix avec taxes
```bash
GET /api/products/{id}/with-tax
```

## Exemples d'utilisation

### Créer et calculer les taxes d'un produit
```bash
# 1. Créer un produit
curl --location 'http://localhost:8080/api/products' --header 'Content-Type: application/json' --data '{"name":"iPhone","price":999.99,"country":"FRANCE"}'
```
```bash
# 2. Calculer le prix avec taxes (réponse de l'étape 1 donne l'ID)
curl http://localhost:8080/api/products/1/with-tax
```

**Réponse exemple :**
```json
{
  "id": 1,
  "name": "Laptop",
  "basePrice": 1000.00,
  "country": "FRANCE",
  "taxAmount": 200.00,
  "finalPrice": 1200.00,
  "taxStrategyUsed": "France Tax Strategy (20% VAT)"
}
```

## Taux de taxation par pays

- **US** : 8.5% (sales tax)
- **CANADA** : 12% (5% GST + 7% PST)
- **FRANCE** : 20% (TVA)

## Structure du projet

```
src/
├── main/java/com/example/taxcalculator/
│   ├── controller/    # Contrôleurs REST
│   ├── dto/           # Objects de transfert
│   ├── model/         # Entités métier
│   ├── service/       # Services business
│   └── strategy/      # Pattern Stratégie (taxes)
└── test/              # Tests unitaires et d'intégration
```

## Pattern Stratégie implémenté

Le calcul des taxes utilise le pattern Stratégie :
- `TaxStrategy` : Interface commune
- `UsTaxStrategy`, `CanadaTaxStrategy`, `FranceTaxStrategy` : Implémentations spécifiques
- `TaxCalculationService` : Sélectionne la stratégie appropriée

## Tests

Couverture complète incluant :
- Tests unitaires des stratégies
- Tests d'intégration de l'API REST
- Tests du pattern Stratégie