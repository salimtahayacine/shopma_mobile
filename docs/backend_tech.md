# ShopMa — Backend (Bonus) : Choix techniques

> Backend personnalisé en **Java Spring Boot** + **PostgreSQL**, exposant des endpoints **compatibles FakeStoreAPI** avec des produits marocains (prix en **MAD**). L'app mobile pointe sur ce backend, avec **fallback configurable** vers FakeStoreAPI.

---

## 1. Stack technique

| Couche | Technologie | Version cible |
|--------|-------------|---------------|
| Langage | Java | 17 (LTS) |
| Framework | Spring Boot | 3.3.x |
| Web | Spring Web (REST) | inclus |
| Persistance | Spring Data JPA (Hibernate) | inclus |
| Base de données | **PostgreSQL** | 15+ |
| Build | Maven | 3.9+ |
| Mapping JSON | Jackson | inclus |
| Doc API | springdoc-openapi (Swagger UI) | 2.x |
| Dév local DB | Docker (image `postgres:15`) — optionnel | — |

---

## 2. Compatibilité FakeStoreAPI

Le backend **reproduit le contrat** de FakeStoreAPI pour que l'app mobile puisse basculer d'une URL à l'autre sans changer le code Retrofit.

**Base URL (local)** : `http://10.0.2.2:8080` (émulateur Android → localhost de la machine hôte).

### Endpoints exposés
| Méthode | URL | Retour | Équivalent FakeStore |
|---------|-----|--------|----------------------|
| GET | `/products` | `List<Product>` | ✔ identique |
| GET | `/products/categories` | `List<String>` | ✔ identique |
| GET | `/products/category/{nom}` | `List<Product>` | ✔ identique |
| GET | `/products/{id}` | `Product` | ✔ identique |

### Contrat JSON `Product` (strictement identique au mobile)
```json
{
  "id": 1,
  "title": "Sac à dos Atlas",
  "price": 349.0,
  "description": "Sac à dos résistant pour le quotidien...",
  "category": "men's clothing",
  "image": "https://.../sac-atlas.jpg",
  "rating": { "rate": 4.2, "count": 87 }
}
```
> ⚠️ Les clés JSON (`title`, `price`, `category`, `image`, `rating.rate`, `rating.count`) restent **en anglais** pour rester compatibles avec le modèle `Product` du mobile. Seules les **valeurs** sont marocaines et les **prix en MAD**.

### Catégories (mêmes clés que FakeStore)
`electronics`, `jewelery`, `men's clothing`, `women's clothing`.

---

## 3. Modèle de données (PostgreSQL)

### Table `products`
| Colonne | Type SQL | Notes |
|---------|----------|-------|
| id | BIGSERIAL PK | auto |
| title | VARCHAR(255) | |
| price | NUMERIC(10,2) | MAD |
| description | TEXT | |
| category | VARCHAR(100) | une des 4 catégories |
| image | VARCHAR(512) | URL |
| rating_rate | NUMERIC(2,1) | exposé en `rating.rate` |
| rating_count | INTEGER | exposé en `rating.count` |

> `rating` est un objet imbriqué côté JSON mais aplati en 2 colonnes côté DB (mapping via `@Embeddable Rating` ou `@JsonProperty`).

---

## 4. Architecture du backend

```
ma.shopma.api
├── ShopMaApiApplication.java        (main Spring Boot)
├── controller/
│   └── ProductController.java       (les 4 endpoints REST)
├── service/
│   └── ProductService.java          (logique métier)
├── repository/
│   └── ProductRepository.java       (Spring Data JPA)
├── entity/
│   ├── Product.java                 (@Entity)
│   └── Rating.java                  (@Embeddable)
├── dto/
│   └── ProductDTO.java              (forme JSON compatible FakeStore)
└── config/
    ├── DataSeeder.java              (insertion des produits marocains au démarrage)
    └── CorsConfig.java              (CORS pour tests)
```

Fichier `src/main/resources/application.properties` :
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/shopma
spring.datasource.username=shopma
spring.datasource.password=shopma
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
server.port=8080
```

---

## 5. Lien avec l'app mobile (fallback configurable)

Côté mobile, l'URL de base est centralisée dans `RetrofitClient` :
```java
// ma.shopma.app.api.RetrofitClient
public static final String BASE_URL_SPRINGBOOT = "http://10.0.2.2:8080/";
public static final String BASE_URL_FAKESTORE  = "https://fakestoreapi.com/";
// Basculer ici (ou via une constante BuildConfig) en cas d'indisponibilité du backend.
public static final String BASE_URL = BASE_URL_SPRINGBOOT;
```
> Comme les contrats JSON sont identiques, le changement d'URL **ne casse rien**. Le mobile reste fonctionnel même si le backend est éteint (il suffit de repointer sur FakeStoreAPI).

---

## 6. Démarrage local (pour la correction)

```bash
# 1. Lancer PostgreSQL (Docker)
docker run --name shopma-pg -e POSTGRES_DB=shopma \
  -e POSTGRES_USER=shopma -e POSTGRES_PASSWORD=shopma \
  -p 5432:5432 -d postgres:15

# 2. Lancer le backend
cd backend
./mvnw spring-boot:run

# 3. Vérifier
curl http://localhost:8080/products
# Swagger UI : http://localhost:8080/swagger-ui.html
```

> Le `README.md` du dossier `backend/` documentera l'URL, la techno et les endpoints (exigence du bonus).

---

## 7. Déploiement optionnel

Déployable gratuitement sur **Render** ou **Railway** (PostgreSQL managé inclus). Si déployé, l'URL publique remplacera `10.0.2.2:8080` dans `RetrofitClient`.
