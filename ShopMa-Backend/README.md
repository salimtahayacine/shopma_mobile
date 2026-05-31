# ShopMa — Backend (Spring Boot + PostgreSQL)

Backend REST **compatible FakeStoreAPI**, avec un catalogue de **produits marocains** dont les **prix sont en MAD**. L'application mobile ShopMa peut pointer indifféremment sur ce backend ou sur FakeStoreAPI (contrats JSON identiques).

> Ce backend est un **bonus** du projet. L'app mobile fonctionne aussi directement sur `https://fakestoreapi.com`.

---

## 1. Technologies

| | |
|---|---|
| Langage | Java 17 |
| Framework | Spring Boot 3.3.4 (Web + Data JPA) |
| Base de données | PostgreSQL 15 |
| Build | Maven |
| Doc API | Swagger UI (springdoc-openapi) |

---

## 2. Endpoints

Base URL locale : `http://localhost:8080` — depuis l'émulateur Android : `http://10.0.2.2:8080`

| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/products` | Liste de tous les produits |
| GET | `/products/categories` | Liste des catégories (`electronics`, `jewelery`, `men's clothing`, `women's clothing`) |
| GET | `/products/category/{nom}` | Produits d'une catégorie |
| GET | `/products/{id}` | Un produit par son identifiant (404 si absent) |

Exemple de réponse (`GET /products/1`) :
```json
{
  "id": 1,
  "title": "Smartphone Argan X10",
  "price": 2499.0,
  "description": "Smartphone Android 6.5\", 128 Go, double SIM...",
  "category": "electronics",
  "image": "https://fakestoreapi.com/img/61IBBVJvSDL._AC_SY879_.jpg",
  "rating": { "rate": 4.3, "count": 215 }
}
```

Documentation interactive : **http://localhost:8080/swagger-ui.html**

---

## 3. Démarrage local

### Étape 1 — PostgreSQL

Avec Docker (recommandé) :
```bash
docker compose up -d
```
> Démarre PostgreSQL (base `shopma`, user `shopma`, mot de passe `shopma`) sur le port 5432.

Sans Docker : créez une base `shopma` et un utilisateur `shopma`/`shopma`, ou adaptez `src/main/resources/application.properties`.

### Étape 2 — Lancer le backend
```bash
# avec Maven installé
mvn spring-boot:run

# ou packager puis exécuter
mvn clean package
java -jar target/shopma-api-1.0.0.jar
```

Au premier démarrage, **20 produits marocains** sont insérés automatiquement (voir `DataSeeder`).

### Étape 3 — Vérifier
```bash
curl http://localhost:8080/products
curl http://localhost:8080/products/categories
curl "http://localhost:8080/products/category/jewelery"
```

---

## 4. Configuration

`src/main/resources/application.properties` :
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/shopma
spring.datasource.username=shopma
spring.datasource.password=shopma
server.port=8080
```

---

## 5. Branchement avec l'app mobile

Dans `ShopMa-Mobile`, classe `RetrofitClient` :
```java
public static final String BASE_URL_SPRINGBOOT = "http://10.0.2.2:8080/";
public static final String BASE_URL_FAKESTORE  = "https://fakestoreapi.com/";
public static final String BASE_URL = BASE_URL_SPRINGBOOT; // basculer ici si besoin
```
Les contrats JSON étant identiques, changer d'URL ne nécessite aucune autre modification.

---

## 6. Structure du projet

```
ShopMa-Backend/
├── pom.xml
├── docker-compose.yml
└── src/main/java/ma/shopma/api/
    ├── ShopMaApiApplication.java
    ├── controller/ProductController.java
    ├── service/ProductService.java
    ├── repository/ProductRepository.java
    ├── entity/{Product,Rating}.java
    ├── dto/ProductDTO.java
    ├── exception/{ResourceNotFoundException,GlobalExceptionHandler}.java
    └── config/{DataSeeder,CorsConfig}.java
```
