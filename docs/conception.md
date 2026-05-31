# ShopMa — Conception

> Conception technique de l'application : architecture, navigation, modèle de données, structure des packages et flux principaux.

---

## 1. Architecture générale

Application Android **mono-module** en Java, organisée par responsabilités (approche en couches légère, sans framework MVVM imposé) :

```
UI (Activities + Fragment + Adapters)
        │
        ▼
Logique / Accès aux données
  ├── api/    → Retrofit (réseau, FakeStoreAPI)
  ├── db/     → SQLite (panier, commandes)
  └── utils/  → SharedPreferences, Notifications
        │
        ▼
Modèles (POJOs : Product, CartItem, Order)
```

- **Réseau** : Retrofit + Gson (catalogue, recherche).
- **Persistance locale** : SQLite (panier, commandes) + SharedPreferences (session, profil).
- **Cartographie** : Google Maps SDK.
- **Système** : Caméra (Intent), Notifications (NotificationChannel), Permissions runtime.

---

## 2. Structure des packages (`com.shopma`)

```
com.shopma
├── LoginActivity.java
├── AccueilActivity.java          (Dashboard)
├── CatalogueActivity.java
├── PanierActivity.java
├── RechercheActivity.java
├── CommandesActivity.java
├── PointsRetraitActivity.java
├── ProfilActivity.java
│
├── fragments/
│   └── HeaderFragment.java       (réutilisé dans ≥ 4 écrans)
│
├── model/
│   ├── Product.java              (mappe le JSON FakeStoreAPI)
│   ├── Rating.java
│   ├── CartItem.java             (ligne table panier)
│   └── Order.java                (ligne table commandes)
│
├── api/
│   ├── ApiService.java           (interface Retrofit)
│   └── RetrofitClient.java       (singleton Retrofit)
│
├── db/
│   └── DatabaseHelper.java       (SQLiteOpenHelper : panier + commandes)
│
├── adapters/
│   ├── ProductAdapter.java       (BaseAdapter — catalogue & recherche)
│   ├── CartAdapter.java          (BaseAdapter — panier)
│   └── OrderAdapter.java         (BaseAdapter — commandes + badge)
│
└── utils/
    ├── PrefManager.java          (wrapper SharedPreferences)
    └── NotificationHelper.java   (canal + notification commande)
```

---

## 3. Schéma de navigation

```
                       ┌──────────────┐
                       │ LoginActivity │
                       └──────┬───────┘
                              │ (login OK, extra: nom utilisateur)
                              ▼
                     ┌──────────────────┐
        ┌────────────│  AccueilActivity │────────────┐
        │            │   (Dashboard)    │            │
        │            └───┬────┬────┬────┘            │
   (Toolbar panier)      │    │    │          (Toolbar profil)
        │       ┌────────┘    │    └────────┐        │
        ▼       ▼             ▼             ▼        ▼
 ┌───────────┐ ┌────────────┐ ┌──────────┐ ┌──────────────────┐ ┌─────────────┐
 │  Panier   │ │ Catalogue  │ │Recherche │ │ PointsRetrait    │ │   Profil    │
 │ Activity  │ │ Activity   │ │ Activity │ │ Activity (Maps)  │ │  Activity   │
 └───────────┘ └────────────┘ └──────────┘ └──────────────────┘ └──────┬──────┘
                                                                        │
                                                                        ▼
                                                              ┌───────────────────┐
                                                              │ CommandesActivity │
                                                              └───────────────────┘
```

- **Traits depuis les cartes** du dashboard → Catalogue / Recherche / Points de retrait.
- **Traits depuis la Toolbar** (panier, profil) → présents dans plusieurs écrans.
- Toutes les navigations : **Intents explicites**.
- Le **nom de l'utilisateur** est passé en `extra` (`EXTRA_USERNAME`) à chaque Activity intégrant le HeaderFragment.

### Écrans utilisant le HeaderFragment (≥ 4 requis)
Catalogue, Panier, Recherche, Commandes → **4 écrans** ✔

---

## 4. Modèle de données

### 4.1 POJOs réseau
```
Product { int id; String title; double price; String description; String category; String image; Rating rating; }
Rating  { double rate; int count; }
```

### 4.2 Entités locales (SQLite)
```
CartItem { long id; int productId; String title; double price; int quantity; }
Order    { long id; String date; int nbArticles; double montantTotal; String statut; }
```

### 4.3 Schéma SQLite (`shopma.db`, version 1)
```sql
CREATE TABLE panier (
  id         INTEGER PRIMARY KEY AUTOINCREMENT,
  product_id INTEGER,
  title      TEXT,
  price      REAL,
  quantity   INTEGER
);

CREATE TABLE commandes (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  date          TEXT,
  nb_articles   INTEGER,
  montant_total REAL,
  statut        TEXT          -- 'en_cours' | 'livree'
);
```

### 4.4 Opérations DatabaseHelper
| Méthode | Rôle |
|---------|------|
| `onCreate` / `onUpgrade` | Création / migration des tables |
| `ajouterAuPanier(CartItem)` | Insert (ou +1 quantité si product_id existe) |
| `lirePanier()` | `List<CartItem>` |
| `mettreAJourQuantite(id, qty)` | Update quantité (si qty=0 → suppression) |
| `supprimerArticlePanier(id)` | Delete une ligne |
| `viderPanier()` | Delete all (après commande) |
| `compterArticlesPanier()` | Somme des quantités → badge Toolbar |
| `totalPanier()` | Somme price×quantity |
| `ajouterCommande(Order)` | Insert commande |
| `lireCommandes()` | `List<Order>` (historique, plus récentes d'abord) |

---

## 5. Gestion de la session & préférences

`PrefManager` (wrapper `SharedPreferences`, fichier `shopma_prefs`) :

| Clé | Type | Usage |
|-----|------|-------|
| `remember_me` | bool | Case « Se rappeler de moi » |
| `saved_email` | String | Email mémorisé (login) |
| `username` | String | Nom affiché (HeaderFragment) |
| `profile_name` | String | Nom du profil |
| `profile_email` | String | Email du profil |
| `profile_address` | String | Adresse de livraison |
| `profile_photo` | String | Chemin de la photo (caméra) |

---

## 6. Flux principaux

### 6.1 Ajout au panier (Catalogue)
`Clic produit → DatabaseHelper.ajouterAuPanier() → Toast confirmation → refresh badge (HeaderFragment + Toolbar)`

### 6.2 Passer commande (Panier)
```
Lire panier (SQLite)
  → total + nb articles
  → ajouterCommande(Order{date, nb, total, 'en_cours'})
  → viderPanier()
  → NotificationHelper.notifierCommande()
  → refresh UI (panier vide, badge = 0)
```

### 6.3 Chargement catalogue (Retrofit)
```
extra "category" ?
  oui → ApiService.getProductsByCategory(cat)
  non → ApiService.getAllProducts()
onResponse → ProductAdapter.setData(list)
onFailure  → TextView "Erreur réseau, réessayez" (pas de crash)
```

---

## 7. Permissions runtime

| Permission | Écran | Déclencheur |
|------------|-------|-------------|
| `CAMERA` | ProfilActivity | Prise de photo |
| `ACCESS_FINE_LOCATION` | PointsRetraitActivity | Affichage position sur la carte |
| `POST_NOTIFICATIONS` (API 33+) | PanierActivity | Notification de commande |

Pattern : `checkSelfPermission` → si refusé `requestPermissions` → callback `onRequestPermissionsResult` → si refus définitif : message clair, **pas de crash**.

---

## 8. Ressources UI

- `res/layout/` : 1 layout par Activity + `fragment_header.xml` + items de liste (`item_product.xml`, `item_cart.xml`, `item_order.xml`).
- `res/menu/menu_toolbar.xml` : actions panier (badge via `actionLayout`) + profil.
- `res/values/` : `strings.xml`, `colors.xml`, `dimens.xml`, `themes.xml`.
- `res/drawable/` : icônes, fonds de cartes, fond du badge de statut.
- `res/xml/file_paths.xml` : FileProvider (photo caméra).
