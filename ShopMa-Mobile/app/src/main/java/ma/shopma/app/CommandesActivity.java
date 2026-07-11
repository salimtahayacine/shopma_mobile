package ma.shopma.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ma.shopma.app.adapters.OrderAdapter;
import ma.shopma.app.db.DatabaseHelper;
import ma.shopma.app.fragments.HeaderFragment;

import java.util.List;

import ma.shopma.app.model.Order;

/** Historique des commandes avec badge de statut coloré. */
public class CommandesActivity extends AppCompatActivity {

    public static final String EXTRA_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commandes);

        String username = getIntent().getStringExtra(EXTRA_USERNAME);
        if (username == null) username = "";

        // HeaderFragment
        HeaderFragment header = HeaderFragment.newInstance(username);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_header, header)
                .commit();

        ListView listView = findViewById(R.id.lv_commandes);
        TextView tvAucune = findViewById(R.id.tv_aucune_commande);

        OrderAdapter adapter = new OrderAdapter(this);
        listView.setAdapter(adapter);

        DatabaseHelper.DB_EXECUTOR.execute(() -> {
            List<Order> commandes = DatabaseHelper.getInstance(this).lireCommandes();
            runOnUiThread(() -> {
                adapter.setData(commandes);
                tvAucune.setVisibility(commandes.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }
}
