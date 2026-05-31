# ShopMa — Spécifications

> Application mobile Android e-commerce (marketplace). Langage **Java** uniquement (Kotlin interdit).
> Document dérivé du cahier des charges *Projet_ShopMa.pdf*.

---

## 1. Problématique

**ShopMa** est une application Android de type marketplace e-commerce. L'utilisateur peut :

- parcourir un catalogue de produits organisé par catégories,
- ajouter des produits à un panier,
- passer des commandes,
- suivre l'historique de ses achats,
- localiser les points de retrait sur une carte.

Le catalogue est chargé depuis une API REST externe : **FakeStoreAPI** (`https://fakestoreapi.com`).

---

## 2. Exigences fonctionnelles

| # | Exigence | Écran(s) | Concept clé |
|---|----------|----------|-------------|
| F1 | Authentification avec validation de formulaire + « Se rappeler de moi » | LoginActivity | SharedPreferences |
| F2 | Dashboard : 6 cartes catégories + Toolbar (panier badgé + profil) | AccueilActivity | GridLayout, Toolbar |
| F3 | Catalogue produits chargé via Retrofit, filtrable par catégorie | CatalogueActivity | Retrofit, ListView, BaseAdapter |
| F4 | Panier persistant, gestion des quantités, calcul du total | PanierActivity | SQLite |
| F5 | Passage de commande → transfert vers table commandes + vidage panier + notification | PanierActivity | SQLite, Notifications |
| F6 | Recherche par catégorie avec suggestions | RechercheActivity | Retrofit, ListView |
| F7 | Historique des commandes avec badge de statut coloré | CommandesActivity | SQLite, BaseAdapter |
| F8 | Carte des points de retrait (≥ 4 marqueurs au Maroc) | PointsRetraitActivity | Google Maps SDK |
| F9 | Profil : photo (caméra), infos, sauvegarde des préférences | ProfilActivity | Caméra, SharedPreferences |
| F10 | Partage d'un produit / appel service client | Catalogue/Profil | Intent implicite |

---

## 3. Spécification des écrans (8 Activities)

### 3.1 LoginActivity
- Logo, titre, champ email, champ mot de passe, case « Se rappeler de moi », bouton Connexion.
- **Validation** : champs non vides, mot de passe ≥ 6 caractères.
- Identifiants vérifiables **en dur** (ex. `user@shopma.ma` / `123456`).
- Si la case est cochée : email sauvegardé en SharedPreferences et restauré au prochain lancement.

### 3.2 AccueilActivity (Dashboard)
- **Toolbar** : titre app + icône panier (avec **badge** du nombre d'articles) + icône profil.
- Message de bienvenue (`Bonjour, <nom>`).
- **Grille 2×3** de cartes : 4 catégories produits + « Tous les produits » + « Points de retrait ».
- Clic carte catégorie → CatalogueActivity (catégorie en extra, ou sans extra pour « Tous »).
- Clic carte « Points de retrait » → PointsRetraitActivity.

### 3.3 CatalogueActivity
- **HeaderFragment** en haut (nom utilisateur + nombre d'articles panier).
- **ListView** de produits chargés via Retrofit.
  - Catégorie en extra → `GET /products/category/{nom}`.
  - Sinon → `GET /products`.
- Chaque item : titre, catégorie, prix (+ image via Glide).
- **Adapter personnalisé** (BaseAdapter avec `getView()` + recyclage `convertView`).
- Clic produit → ajout au panier (insertion SQLite) + mise à jour badge.

### 3.4 PanierActivity
- HeaderFragment en haut.
- ListView des articles du panier (SQLite) : nom, quantité, prix.
- **Total** calculé et affiché en bas.
- Bouton **« Passer commande »** → insertion dans table `commandes`, vidage `panier`, **notification locale** de confirmation.

### 3.5 RechercheActivity
- HeaderFragment en haut.
- Champ de saisie + bouton OK.
- Suggestions de catégories (`electronics`, `jewelery`, `men's clothing`, `women's clothing`).
- ListView des résultats via `GET /products/category/{nom}` (même Adapter que catalogue).

### 3.6 CommandesActivity
- Accessible depuis le profil.
- HeaderFragment en haut.
- ListView des commandes passées (SQLite) : numéro, date, nb articles, montant total, **badge de statut** (En cours / Livrée).
- Adapter personnalisé pour le badge coloré.

### 3.7 PointsRetraitActivity
- Titre en haut + **SupportMapFragment** (reste de l'écran).
- ≥ 4 marqueurs sur des points de retrait au Maroc (titre + snippet adresse).
- Caméra centrée sur le Maroc, zoom adapté.

### 3.8 ProfilActivity
- Photo utilisateur via `ACTION_IMAGE_CAPTURE` (caméra).
- Nom, email, adresse de livraison (sauvegardés en SharedPreferences).
- Bouton « Historique des commandes » → CommandesActivity.
- Bouton « Déconnexion » → LoginActivity.

---

## 4. Spécification de l'API FakeStoreAPI

**Base URL** : `https://fakestoreapi.com` — gratuite, publique, sans authentification.

### 4.1 Modèle produit (JSON)
```json
{
  "id": 1,
  "title": "Fjallraven Foldsack No. 1 Backpack",
  "price": 109.95,
  "description": "Your perfect pack for everyday use...",
  "category": "men's clothing",
  "image": "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
  "rating": { "rate": 3.9, "count": 120 }
}
```
Classe Java `Product` : `int id`, `String title`, `double price`, `String description`, `String category`, `String image`. Les noms correspondent **exactement** aux clés JSON.

### 4.2 Endpoints
| URL | Retour |
|-----|--------|
| `GET /products` | Liste de 20 produits |
| `GET /products/categories` | `["electronics","jewelery","men's clothing","women's clothing"]` |
| `GET /products/category/{nom}` | Produits filtrés par catégorie |
| `GET /products/{id}` | Un produit |

### 4.3 Interface Retrofit `ApiService`
- `@GET("products")` → `Call<List<Product>>`
- `@GET("products/categories")` → `Call<List<String>>`
- `@GET("products/category/{cat}")` + `@Path("cat")` → `Call<List<Product>>`

---

## 5. Base de données locale (SQLite)

### Table `panier`
| Colonne | Type | Description |
|---------|------|-------------|
| id | INTEGER PK AUTOINCREMENT | Identifiant |
| product_id | INTEGER | ID produit (API) |
| title | TEXT | Nom du produit |
| price | REAL | Prix unitaire |
| quantity | INTEGER | Quantité |

### Table `commandes`
| Colonne | Type | Description |
|---------|------|-------------|
| id | INTEGER PK AUTOINCREMENT | Identifiant |
| date | TEXT | Date (`dd/MM/yyyy`) |
| nb_articles | INTEGER | Nombre total d'articles |
| montant_total | REAL | Montant total |
| statut | TEXT | `en_cours` ou `livree` |

**DatabaseHelper** implémente : création des tables, ajout panier, lecture panier, suppression panier (après commande), ajout commande, lecture historique, comptage des articles (badge Toolbar).

---

## 6. Contraintes techniques (obligatoires)

| Concept | Utilisation attendue |
|---------|----------------------|
| Layouts | LinearLayout, GridLayout (dashboard), ConstraintLayout/RelativeLayout. Ressources externalisées. |
| Toolbar | Icônes panier (badge) + profil. |
| findViewById | Dans toutes les Activities. |
| ListView + BaseAdapter | Catalogue, panier, commandes, recherche. ≥ 1 Adapter perso avec `getView()` + recyclage. |
| Fragment | `HeaderFragment` réutilisable dans ≥ 4 Activities. |
| SharedPreferences | Email (Se rappeler de moi) + infos profil. |
| SQLite | 2 tables min (panier, commandes). DatabaseHelper complet. |
| Intents explicites | Navigation entre toutes les Activities avec extras. |
| Intents implicites | ≥ 1 : partage produit par email **ou** appel service client. |
| Retrofit + Gson | Catalogue. ≥ 2 endpoints (liste + filtre). |
| Google Maps SDK | MapFragment + ≥ 4 marqueurs. |
| Caméra | Photo profil via `ACTION_IMAGE_CAPTURE`. |
| Notifications locales | Confirmation commande. **Canal obligatoire**. |
| Permissions runtime | CAMERA + ACCESS_FINE_LOCATION avec `checkSelfPermission`/`requestPermissions`. |

---

## 7. Consignes générales

1. Projet **strictement individuel**.
2. Langage **Java** (Kotlin interdit).
3. Chaînes → `strings.xml`, couleurs → `colors.xml`, dimensions → `dimens.xml`.
4. Fonctionne **sans crash** sur émulateur **API 24+**.
5. Permissions runtime gérées : pas de crash si refus.
6. HeaderFragment dans ≥ 4 Activities sans duplication.
7. Gestion des erreurs réseau : message clair, pas de crash.
8. Liberté sur le design tant que la structure des wireframes est respectée.
9. Picasso/Glide autorisés (non obligatoires).
10. Livrable : projet Android Studio complet en `.zip`.

---

## 8. Bonus — Backend personnalisé

Optionnel mais valorisé. Voir [`backend_tech.md`](backend_tech.md). Doit être **fonctionnel et documenté** (README : URL, techno, endpoints, instructions de démarrage).
