package ma.shopma.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import ma.shopma.app.api.ApiService;
import ma.shopma.app.api.RetrofitClient;
import ma.shopma.app.db.DatabaseHelper;
import ma.shopma.app.model.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Dashboard redesigné ShopMa.
 * - Toolbar custom : hamburger + ShopMa + panier badgé
 * - Salutation + barre de recherche
 * - Hero banner promo
 * - Catégories bento grid (icônes vectorielles + fonds colorés)
 * - Section "Tendances" : HorizontalScrollView peuplé via Retrofit
 * - BottomNavigationView fixe
 */
public class AccueilActivity extends AppCompatActivity {

    public static final String EXTRA_USERNAME = "username";

    private String username;
    private TextView tvBadge;
    private LinearLayout llTendances;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        username = getIntent().getStringExtra(EXTRA_USERNAME);
        if (username == null) username = "";

        // ─── Vues ───
        tvBadge    = findViewById(R.id.tv_badge);
        llTendances = findViewById(R.id.ll_tendances);
        TextView tvGreeting = findViewById(R.id.tv_greeting);

        // Salutation personnalisée (capital first letter)
        String displayed = username.isEmpty() ? "vous" :
                Character.toUpperCase(username.charAt(0)) + username.substring(1);
        tvGreeting.setText(String.format(getString(R.string.welcome_message), displayed));

        // ─── Toolbar : panier ───
        findViewById(R.id.fl_cart).setOnClickListener(v -> ouvrirPanier());
        rafraichirBadge();

        // ─── Hamburger menu → profil (drawer non implémenté → profil) ───
        findViewById(R.id.btn_menu).setOnClickListener(v -> {
            Intent i = new Intent(this, ProfilActivity.class);
            i.putExtra(EXTRA_USERNAME, username);
            startActivity(i);
        });

        // ─── Barre de recherche ───
        findViewById(R.id.ll_search_bar).setOnClickListener(v -> {
            Intent iRecherche = new Intent(this, RechercheActivity.class);
            iRecherche.putExtra(RechercheActivity.EXTRA_USERNAME, username);
            startActivity(iRecherche);
        });

        // ─── Hero "En profiter" → Électronique ───
        findViewById(R.id.btn_hero).setOnClickListener(v ->
                ouvrirCatalogue("electronics"));

        // ─── Catégories ───
        findViewById(R.id.card_all).setOnClickListener(v -> ouvrirCatalogue(null));
        findViewById(R.id.card_electronics).setOnClickListener(v -> ouvrirCatalogue("electronics"));
        findViewById(R.id.card_jewelery).setOnClickListener(v -> ouvrirCatalogue("jewelery"));
        findViewById(R.id.card_mens).setOnClickListener(v -> ouvrirCatalogue("men's clothing"));
        findViewById(R.id.card_womens).setOnClickListener(v -> ouvrirCatalogue("women's clothing"));
        findViewById(R.id.card_pickup).setOnClickListener(v ->
                startActivity(new Intent(this, PointsRetraitActivity.class)));

        // ─── "Voir tout" ───
        findViewById(R.id.tv_voir_tout).setOnClickListener(v -> ouvrirCatalogue(null));

        // ─── BottomNavigationView ───
        setupBottomNav();

        // ─── Charger les Tendances via API ───
        chargerTendances();
    }

    @Override
    protected void onResume() {
        super.onResume();
        rafraichirBadge();
        // Marquer "Accueil" comme sélectionné à chaque retour
        BottomNavigationView bnv = findViewById(R.id.bottom_nav);
        bnv.setSelectedItemId(R.id.nav_home);
    }

    // ─── Navigation ─────────────────────────────────────────────────────────

    private void setupBottomNav() {
        BottomNavigationView bnv = findViewById(R.id.bottom_nav);
        bnv.setSelectedItemId(R.id.nav_home);

        bnv.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Déjà ici — scroll vers le haut
                return true;
            } else if (id == R.id.nav_categories) {
                ouvrirCatalogue(null);
                return true;
            } else if (id == R.id.nav_cart) {
                ouvrirPanier();
                return true;
            } else if (id == R.id.nav_orders) {
                Intent i = new Intent(this, CommandesActivity.class);
                i.putExtra(EXTRA_USERNAME, username);
                startActivity(i);
                return true;
            }
            return false;
        });
    }

    private void ouvrirCatalogue(String category) {
        Intent i = new Intent(this, CatalogueActivity.class);
        i.putExtra(EXTRA_USERNAME, username);
        if (category != null) i.putExtra(CatalogueActivity.EXTRA_CATEGORY, category);
        startActivity(i);
    }

    private void ouvrirPanier() {
        Intent i = new Intent(this, PanierActivity.class);
        i.putExtra(EXTRA_USERNAME, username);
        startActivity(i);
    }

    // ─── Badge panier ────────────────────────────────────────────────────────

    private void rafraichirBadge() {
        DatabaseHelper.DB_EXECUTOR.execute(() -> {
            int count = DatabaseHelper.getInstance(this).compterArticlesPanier();
            runOnUiThread(() -> {
                if (count > 0) {
                    tvBadge.setText(String.valueOf(count));
                    tvBadge.setVisibility(View.VISIBLE);
                } else {
                    tvBadge.setVisibility(View.GONE);
                }
            });
        });
    }

    // ─── Section Tendances (Retrofit) ────────────────────────────────────────

    private void chargerTendances() {
        ApiService api = RetrofitClient.getService();
        api.getAllProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> produits = response.body();
                    // Afficher les 6 premiers
                    int nbMax = Math.min(produits.size(), 6);
                    for (int i = 0; i < nbMax; i++) {
                        ajouterCarteTendance(produits.get(i));
                    }
                }
                // Pas d'erreur affichée si le chargement échoue — section simplement vide
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                // Silencieux — ne pas interrompre l'UX principale
            }
        });
    }

    /**
     * Gonfle un item_trending_product.xml et l'ajoute au HorizontalScrollView.
     */
    private void ajouterCarteTendance(Product produit) {
        View card = LayoutInflater.from(this)
                .inflate(R.layout.item_trending_product, llTendances, false);

        ImageView ivImage = card.findViewById(R.id.iv_trending_image);
        TextView tvName   = card.findViewById(R.id.tv_trending_name);
        TextView tvPrice  = card.findViewById(R.id.tv_trending_price);

        tvName.setText(produit.getTitle());
        tvPrice.setText(String.format("%.0f MAD", produit.getPrice()));

        // Charger l'image via Glide (URL produit)
        if (produit.getImage() != null && !produit.getImage().isEmpty()) {
            Glide.with(this)
                    .load(produit.getImage())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivImage);
        }

        // Click → CatalogueActivity (catégorie du produit)
        card.setOnClickListener(v -> {
            if (produit.getCategory() != null) {
                ouvrirCatalogue(produit.getCategory());
            }
        });

        llTendances.addView(card);
    }
}
