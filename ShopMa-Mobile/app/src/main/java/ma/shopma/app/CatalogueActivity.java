package ma.shopma.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ma.shopma.app.adapters.ProductAdapter;
import ma.shopma.app.api.RetrofitClient;
import ma.shopma.app.db.DatabaseHelper;
import ma.shopma.app.fragments.HeaderFragment;
import ma.shopma.app.model.CartItem;
import ma.shopma.app.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Affiche le catalogue. Si EXTRA_CATEGORY est fourni, filtre par catégorie.
 * Gestion erreur réseau + ajout au panier SQLite + intent implicite (partage).
 */
public class CatalogueActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY = "category";
    public static final String EXTRA_USERNAME = "username";

    private HeaderFragment headerFragment;
    private ProductAdapter adapter;
    private TextView tvError;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue);

        username = getIntent().getStringExtra(EXTRA_USERNAME);
        if (username == null) username = "";
        String category = getIntent().getStringExtra(EXTRA_CATEGORY);

        // HeaderFragment
        headerFragment = HeaderFragment.newInstance(username);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_header, headerFragment)
                .commit();

        // Titre
        TextView tvTitre = findViewById(R.id.tv_titre_catalogue);
        tvTitre.setText(category != null ? category : getString(R.string.card_all));

        tvError = findViewById(R.id.tv_error);
        ListView listView = findViewById(R.id.lv_produits);

        adapter = new ProductAdapter(this);
        adapter.setOnAddToCartListener(product -> ajouterAuPanier(product));
        listView.setAdapter(adapter);

        // Clic long sur un item → partage par email (intent implicite)
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Product p = adapter.getItem(position);
            partagerProduit(p);
            return true;
        });

        chargerProduits(category);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (headerFragment != null) headerFragment.refresh();
    }

    private void chargerProduits(String category) {
        Call<List<Product>> call;
        if (category != null && !category.isEmpty()) {
            call = RetrofitClient.getService().getProductsByCategory(category);
        } else {
            call = RetrofitClient.getService().getAllProducts();
        }

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                tvError.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setData(response.body());
                } else {
                    tvError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                tvError.setVisibility(View.VISIBLE);
                tvError.setText(getString(R.string.error_network));
            }
        });
    }

    private void ajouterAuPanier(Product product) {
        CartItem item = new CartItem(product.getId(), product.getTitle(), product.getPrice(), 1);
        DatabaseHelper.getInstance(this).ajouterAuPanier(item);
        Toast.makeText(this, R.string.added_to_cart, Toast.LENGTH_SHORT).show();
        if (headerFragment != null) headerFragment.refresh();
    }

    /** Intent implicite : partage du produit par email. */
    private void partagerProduit(Product product) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Découvrez ce produit sur ShopMa : " + product.getTitle());
        intent.putExtra(Intent.EXTRA_TEXT,
                product.getTitle() + "\nPrix : " + product.getPrice() + " MAD\n" + product.getImage());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
