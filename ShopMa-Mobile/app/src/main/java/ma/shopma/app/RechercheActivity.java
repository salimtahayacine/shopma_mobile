package ma.shopma.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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

/** Recherche par catégorie avec suggestions. */
public class RechercheActivity extends AppCompatActivity {

    public static final String EXTRA_USERNAME = "username";

    private HeaderFragment headerFragment;
    private ProductAdapter adapter;
    private TextView tvAucunResultat;
    private EditText etSearch;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche);

        username = getIntent().getStringExtra(EXTRA_USERNAME);
        if (username == null) username = "";

        headerFragment = HeaderFragment.newInstance(username);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_header, headerFragment)
                .commit();

        etSearch = findViewById(R.id.et_search);
        tvAucunResultat = findViewById(R.id.tv_aucun_resultat);
        ListView listView = findViewById(R.id.lv_resultats);

        adapter = new ProductAdapter(this);
        adapter.setOnAddToCartListener(p -> {
            DatabaseHelper.getInstance(this).ajouterAuPanier(
                    new CartItem(p.getId(), p.getTitle(), p.getPrice(), 1));
            if (headerFragment != null) headerFragment.refresh();
        });
        listView.setAdapter(adapter);

        // Bouton OK
        findViewById(R.id.btn_search).setOnClickListener(v ->
                rechercher(etSearch.getText().toString().trim()));

        // Suggestions (chips)
        findViewById(R.id.chip_electronics).setOnClickListener(v -> remplirEtRechercher("electronics"));
        findViewById(R.id.chip_jewelery).setOnClickListener(v -> remplirEtRechercher("jewelery"));
        findViewById(R.id.chip_mens).setOnClickListener(v -> remplirEtRechercher("men's clothing"));
        findViewById(R.id.chip_womens).setOnClickListener(v -> remplirEtRechercher("women's clothing"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (headerFragment != null) headerFragment.refresh();
    }

    private void remplirEtRechercher(String cat) {
        etSearch.setText(cat);
        rechercher(cat);
    }

    private void rechercher(String cat) {
        if (cat.isEmpty()) return;
        tvAucunResultat.setVisibility(View.GONE);

        RetrofitClient.getService().getProductsByCategory(cat).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    adapter.setData(response.body());
                    tvAucunResultat.setVisibility(View.GONE);
                } else {
                    adapter.setData(null);
                    tvAucunResultat.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                adapter.setData(null);
                tvAucunResultat.setText(R.string.error_network);
                tvAucunResultat.setVisibility(View.VISIBLE);
            }
        });
    }
}
