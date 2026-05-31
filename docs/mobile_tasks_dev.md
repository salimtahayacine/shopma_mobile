# ShopMa — Mobile (Android/Java) : Tâches de développement

> App Android **Java**, package **`ma.shopma.app`**, minSdk 24. Couvre les 7 ateliers.
> Mobile branché sur le **backend Spring Boot** (`http://10.0.2.2:8080/`) avec **fallback FakeStoreAPI**.
> ⚠️ Les fichiers Gradle/Manifest déjà créés utilisent `com.shopma` → à migrer vers `ma.shopma.app` (tâche M0.2).

---

## Phase 0 — Projet & configuration
- [ ] M0.1 Structure Gradle (settings/build/wrapper) — *créée, à vérifier*.
- [ ] M0.2 **Renommer le package** `com.shopma` → `ma.shopma.app` (build.gradle `namespace`/`applicationId`, manifest, arborescence `java/ma/shopma/app/`).
- [ ] M0.3 Dépendances : appcompat, material, constraintlayout, **Retrofit + converter-gson**, **play-services-maps**, play-services-location, **Glide**.
- [ ] M0.4 Permissions manifest : INTERNET, CAMERA, ACCESS_FINE_LOCATION, POST_NOTIFICATIONS.
- [ ] M0.5 Clé Google Maps via `local.properties` (`MAPS_API_KEY`) + placeholder manifest.
- [ ] M0.6 FileProvider + `res/xml/file_paths.xml` (photo caméra).

## Phase 1 — Ressources (atelier Layouts)
- [ ] M1.1 `colors.xml`, `dimens.xml`, `strings.xml` (toutes les chaînes externalisées).
- [ ] M1.2 `themes.xml` (thème Material, Toolbar sans ActionBar par défaut).
- [ ] M1.3 Drawables : icônes panier/profil, fonds de cartes, fond badge statut.
- [ ] M1.4 `menu/menu_toolbar.xml` (action panier avec `actionLayout` badge + action profil).

## Phase 2 — Modèles & couche réseau (atelier Retrofit)
- [ ] M2.1 `model/Product.java`, `model/Rating.java` (clés = JSON exact).
- [ ] M2.2 `model/CartItem.java`, `model/Order.java`.
- [ ] M2.3 `api/ApiService.java` : `getAllProducts()`, `getCategories()`, `getProductsByCategory(@Path)`.
- [ ] M2.4 `api/RetrofitClient.java` : singleton, `BASE_URL` Spring Boot + constante fallback FakeStore.

## Phase 3 — Persistance locale (atelier SQLite + SharedPreferences)
- [ ] M3.1 `db/DatabaseHelper.java` : tables `panier` + `commandes` (onCreate/onUpgrade).
- [ ] M3.2 Méthodes panier : ajouter, lire, maj quantité, supprimer, vider, compter, total.
- [ ] M3.3 Méthodes commandes : ajouter, lire historique.
- [ ] M3.4 `utils/PrefManager.java` (session, remember-me, profil).

## Phase 4 — Fragment réutilisable (atelier Fragments)
- [ ] M4.1 `fragment_header.xml` (nom utilisateur + nb articles panier).
- [ ] M4.2 `fragments/HeaderFragment.java` (lit l'extra nom + DB badge ; méthode `refresh()`).
- [ ] M4.3 Intégrer dans **≥ 4 écrans** : Catalogue, Panier, Recherche, Commandes.

## Phase 5 — Adapters (atelier ListView + BaseAdapter)
- [ ] M5.1 `adapters/ProductAdapter.java` (BaseAdapter, `getView()` + recyclage `convertView`, Glide).
- [ ] M5.2 `adapters/CartAdapter.java` (nom, quantité, prix).
- [ ] M5.3 `adapters/OrderAdapter.java` (numéro, date, nb, total, **badge coloré** En cours/Livrée).
- [ ] M5.4 Items : `item_product.xml`, `item_cart.xml`, `item_order.xml`.

## Phase 6 — Écrans (Activities)
- [ ] M6.1 **LoginActivity** : validation (champs non vides, mdp ≥ 6), identifiants en dur, remember-me (SharedPreferences).
- [ ] M6.2 **AccueilActivity** : Toolbar (panier badgé + profil), message bienvenue, **GridLayout 2×3** de cartes.
- [ ] M6.3 **CatalogueActivity** : HeaderFragment + ListView Retrofit (filtre via extra), clic → ajout panier.
- [ ] M6.4 **PanierActivity** : HeaderFragment + ListView SQLite, total, **Passer commande** (→ commandes + vider + notif).
- [ ] M6.5 **RechercheActivity** : HeaderFragment + champ + suggestions catégories + ListView résultats.
- [ ] M6.6 **CommandesActivity** : HeaderFragment + ListView historique + badge statut.
- [ ] M6.7 **PointsRetraitActivity** : SupportMapFragment + ≥ 4 marqueurs Maroc (titre+snippet), caméra centrée Maroc.
- [ ] M6.8 **ProfilActivity** : photo caméra, infos (SharedPreferences), bouton Historique, bouton Déconnexion.

## Phase 7 — Intégrations système
- [ ] M7.1 **Caméra** (`ACTION_IMAGE_CAPTURE` + FileProvider) dans ProfilActivity.
- [ ] M7.2 **Google Maps** (atelier Maps) : marqueurs + caméra.
- [ ] M7.3 **Notifications locales** : `utils/NotificationHelper.java` + **NotificationChannel** (confirmation commande).
- [ ] M7.4 **Permissions runtime** : CAMERA, ACCESS_FINE_LOCATION, POST_NOTIFICATIONS (pas de crash si refus).
- [ ] M7.5 **Intent implicite** : partage produit par email **ou** appel service client.

## Phase 8 — Robustesse & finition
- [ ] M8.1 Gestion erreurs réseau (`onFailure` → message clair, pas de crash).
- [ ] M8.2 Badge Toolbar/Header rafraîchi à chaque retour d'écran (`onResume`).
- [ ] M8.3 Vérifier nom utilisateur passé en extra à chaque écran avec HeaderFragment.
- [ ] M8.4 Test complet sans crash sur émulateur **API 24+**.
- [ ] M8.5 Vérifier : toutes chaînes/couleurs/dimens externalisées.

---

### Checklist couverture des 7 ateliers (validation finale)
- [ ] Layouts (Linear/Grid/Constraint) + ressources externalisées
- [ ] ListView + BaseAdapter personnalisé (≥ 1)
- [ ] Fragment réutilisable (≥ 4 écrans)
- [ ] SQLite (2 tables) + SharedPreferences
- [ ] Retrofit (≥ 2 endpoints)
- [ ] Google Maps (≥ 4 marqueurs)
- [ ] Caméra + Notifications + Permissions runtime
- [ ] Intents explicites (navigation) + ≥ 1 intent implicite
