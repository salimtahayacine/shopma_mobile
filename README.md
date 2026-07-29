# ShopMa — Application e-commerce Android

Projet de fin de module "Développement Mobile Android" (ENSA Kénitra — Université Ibn Tofail).
Application mobile Android **Java** (marketplace e-commerce) + **backend Spring Boot bonus**.

## Structure du dépôt

```
ShopMa_Mobile/
├── ShopMa-Mobile/      Application Android (Java) — voir ShopMa-Mobile/README.md
├── ShopMa-Backend/     Backend Spring Boot bonus — voir ShopMa-Backend/README.md
├── docs/               Suivi de développement (specs, tâches)
└── Projet_ShopMa.pdf   Cahier des charges original
```

## Démarrage rapide

### 1. Backend (bonus — optionnel, l'app fonctionne aussi sur FakeStoreAPI)

Le plus simple : tout lancer avec **Docker** (app + PostgreSQL en une commande) :

```bash
cd ShopMa-Backend
docker compose up --build
```

L'API est alors disponible sur `http://localhost:8080` (voir [ShopMa-Backend/README.md](ShopMa-Backend/README.md) pour les endpoints, Swagger, et les alternatives sans Docker).

### 2. Mobile

Ouvrir `ShopMa-Mobile/` dans **Android Studio**, laisser Gradle se synchroniser, puis lancer sur un émulateur **API 24+**.

Voir [ShopMa-Mobile/README.md](ShopMa-Mobile/README.md) pour la configuration de la clé Google Maps et le choix de la source de données (backend Spring Boot vs FakeStoreAPI).

### Identifiants de connexion (démo)

```
Email    : youssef@shopma.ma
Password : 123456
```
