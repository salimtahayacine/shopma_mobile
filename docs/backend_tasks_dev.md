# ShopMa — Backend : Tâches de développement

> Backend **Spring Boot + PostgreSQL** compatible FakeStoreAPI (bonus). Cocher au fur et à mesure.
> Cible : 4 endpoints GET identiques à FakeStoreAPI, produits marocains en MAD.

---

## Phase 0 — Initialisation
- [x] B0.1 Projet Maven, Java 17 (testé sur JDK 21), Spring Boot 3.3.4.
- [x] B0.2 Dépendances : `Spring Web`, `Spring Data JPA`, `PostgreSQL Driver`, `springdoc-openapi`, H2 (test).
- [x] B0.3 Package racine : `ma.shopma.api`.
- [x] B0.4 `application.properties` (datasource PostgreSQL, `ddl-auto=update`, port 8080).
- [x] B0.5 `docker-compose.yml` PostgreSQL `postgres:15` (base `shopma`). *(à lancer si Docker Desktop démarré)*

## Phase 1 — Modèle & persistance
- [x] B1.1 `entity/Rating.java` (`@Embeddable` : `rate`, `count`).
- [x] B1.2 `entity/Product.java` (`@Entity` : id, title, price, description, category, image, `@Embedded Rating`).
- [x] B1.3 `repository/ProductRepository.java` (`JpaRepository` + `findByCategory` + `findDistinctCategories`).
- [x] B1.4 Création automatique de la table vérifiée via test d'intégration H2.

## Phase 2 — DTO compatible FakeStoreAPI
- [x] B2.1 `dto/ProductDTO.java` avec clés JSON anglaises + objet `rating{rate,count}`.
- [x] B2.2 Mapping Entity → DTO (`ProductDTO.from(...)`) + test unitaire.
- [x] B2.3 Sérialisation JSON vérifiée identique au modèle mobile (test : `$[0].rating.rate`).

## Phase 3 — Couche service & contrôleur
- [x] B3.1 `service/ProductService.java` : `findAll`, `findByCategory`, `findById`, `findCategories`.
- [x] B3.2 `controller/ProductController.java` :
  - [x] `GET /products` → `List<ProductDTO>`
  - [x] `GET /products/categories` → `List<String>`
  - [x] `GET /products/category/{nom}` → `List<ProductDTO>`
  - [x] `GET /products/{id}` → `ProductDTO` (404 si absent)
- [x] B3.3 Gestion des erreurs (`@RestControllerAdvice` : 404 / 500 JSON propres).

## Phase 4 — Données marocaines (seed)
- [x] B4.1 `config/DataSeeder.java` (`CommandLineRunner`) : insertion si table vide.
- [x] B4.2 **20 produits** répartis sur les 4 catégories (5 chacune).
- [x] B4.3 Titres/descriptions marocaines, **prix en MAD**, URLs d'images valides.
- [x] B4.4 `rating.rate` et `rating.count` renseignés.

## Phase 5 — Qualité & accès mobile
- [x] B5.1 `config/CorsConfig.java` (origines `*` pour tests).
- [x] B5.2 API testée sur PostgreSQL 16-alpine (Docker) : 20 produits, 4 catégories, filtre, 404 — tout OK. *(accès émulateur à valider avec le mobile)*
- [x] B5.3 **Swagger UI** activé (`/swagger-ui.html`).
- [x] B5.4 4 endpoints vérifiés par test d'intégration (MockMvc) + 1 test unitaire mapping. **5/5 verts.**

## Phase 6 — Documentation & livraison
- [x] B6.1 `ShopMa-Backend/README.md` : URL, techno, endpoints, démarrage (Docker + Maven).
- [ ] B6.2 (Optionnel) Déploiement Render/Railway + URL publique → MAJ `RetrofitClient`.
- [x] B6.3 Démarrage validé avec PostgreSQL 16-alpine (Docker) + `mvn spring-boot:run`. Seeder injecte 20 produits au 1er lancement.

---

### Définition de « terminé » (backend)
- Les 4 endpoints répondent avec un JSON **identique au contrat FakeStoreAPI**.
- L'app mobile fonctionne en pointant sur `http://10.0.2.2:8080/` sans modification du code Retrofit (hors URL).
- `README.md` permet de relancer le backend en local en < 5 min.
