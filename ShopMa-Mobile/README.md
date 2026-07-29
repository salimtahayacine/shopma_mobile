# ShopMa — Mobile (Android/Java)

Application e-commerce marketplace Android, package `ma.shopma.app`, minSdk 24 / targetSdk 34.

## Technologies

- Java, layouts Linear/Grid/Constraint, ressources externalisées (`strings.xml`, `colors.xml`, `dimens.xml`)
- Retrofit + Gson (FakeStoreAPI ou backend ShopMa Spring Boot — contrats JSON identiques)
- SQLite (`DatabaseHelper` — tables `panier` et `commandes`) + SharedPreferences (`PrefManager`)
- ListView + `BaseAdapter` personnalisés (Produits, Panier, Commandes)
- `HeaderFragment` réutilisable (Catalogue, Panier, Recherche, Commandes)
- Google Maps SDK, Caméra (`ACTION_IMAGE_CAPTURE`), Notifications locales, Permissions runtime

## Configuration avant de lancer

### 1. Clé Google Maps

Dans `ShopMa-Mobile/local.properties` :

```properties
sdk.dir=<chemin vers votre Android SDK>
MAPS_API_KEY=<votre clé API Google Maps>
```

Sans clé valide, l'application ne plante pas mais la carte (écran "Points de retrait") ne s'affiche pas correctement.

### 2. Source des données produits

Dans [`RetrofitClient.java`](app/src/main/java/ma/shopma/app/api/RetrofitClient.java) :

```java
public static final String BASE_URL = BASE_URL_SPRINGBOOT; // ou BASE_URL_FAKESTORE
```

- `BASE_URL_SPRINGBOOT` (`http://10.0.2.2:8080/`) : pointe sur le backend Spring Boot bonus (voir [ShopMa-Backend/README.md](../ShopMa-Backend/README.md), lançable via **Docker**).
- `BASE_URL_FAKESTORE` (`https://fakestoreapi.com/`) : fonctionne directement sans rien à lancer.

## Lancer le projet

1. Ouvrir le dossier `ShopMa-Mobile` dans Android Studio.
2. Laisser Gradle synchroniser (télécharge le wrapper si absent).
3. Choisir un émulateur **API 24 ou supérieur**.
4. Run ▶.

## Identifiants de connexion (démo, en dur)

```
Email    : youssef@shopma.ma
Password : 123456
```

## Structure

```
app/src/main/java/ma/shopma/app/
├── *Activity.java       8 écrans (Login, Accueil, Catalogue, Panier, Recherche, Commandes, PointsRetrait, Profil)
├── adapters/            BaseAdapter personnalisés (Product, Cart, Order)
├── api/                 Retrofit (ApiService, RetrofitClient)
├── db/                  DatabaseHelper (SQLite)
├── fragments/           HeaderFragment réutilisable
├── model/               Product, CartItem, Order
└── utils/               PrefManager (SharedPreferences), NotificationHelper
```
