package ma.shopma.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ma.shopma.app.adapters.CartAdapter;
import ma.shopma.app.db.DatabaseHelper;
import ma.shopma.app.fragments.HeaderFragment;
import ma.shopma.app.model.CartItem;
import ma.shopma.app.model.Order;
import ma.shopma.app.utils.NotificationHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Panier SQLite + passage de commande + notification locale.
 */
public class PanierActivity extends AppCompatActivity {

    public static final String EXTRA_USERNAME = "username";
    private static final int REQ_NOTIF = 102;

    private HeaderFragment headerFragment;
    private CartAdapter adapter;
    private TextView tvTotal, tvPanierVide;
    private DatabaseHelper db;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier);

        username = getIntent().getStringExtra(EXTRA_USERNAME);
        if (username == null) username = "";
        db = DatabaseHelper.getInstance(this);

        headerFragment = HeaderFragment.newInstance(username);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_header, headerFragment)
                .commit();

        tvTotal = findViewById(R.id.tv_total);
        tvPanierVide = findViewById(R.id.tv_panier_vide);
        ListView listView = findViewById(R.id.lv_panier);
        Button btnCommander = findViewById(R.id.btn_commander);

        adapter = new CartAdapter(this);
        adapter.setOnQuantityChangeListener((item, newQty) -> {
            DatabaseHelper.DB_EXECUTOR.execute(() -> {
                db.mettreAJourQuantite(item.getId(), newQty);
                runOnUiThread(this::chargerPanier);
            });
        });
        listView.setAdapter(adapter);

        btnCommander.setOnClickListener(v -> confirmerCommande());

        chargerPanier();
    }

    @Override
    protected void onResume() {
        super.onResume();
        chargerPanier();
        if (headerFragment != null) headerFragment.refresh();
    }

    private void chargerPanier() {
        DatabaseHelper.DB_EXECUTOR.execute(() -> {
            List<CartItem> items = db.lirePanier();
            double total = db.totalPanier();
            runOnUiThread(() -> {
                adapter.setData(items);
                tvTotal.setText(String.format("%.2f MAD", total));
                tvPanierVide.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }

    private void confirmerCommande() {
        DatabaseHelper.DB_EXECUTOR.execute(() -> {
            int count = db.compterArticlesPanier();
            double total = db.totalPanier();
            runOnUiThread(() -> {
                if (count == 0) {
                    Toast.makeText(this, R.string.panier_vide, Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(this)
                        .setTitle(R.string.confirm_commande_title)
                        .setMessage(getString(R.string.confirm_commande_msg, String.format("%.2f", total)))
                        .setPositiveButton(R.string.btn_oui, (d, w) -> passerCommande(total))
                        .setNegativeButton(R.string.btn_non, null)
                        .show();
            });
        });
    }

    private void passerCommande(double total) {
        DatabaseHelper.DB_EXECUTOR.execute(() -> {
            int nbArticles = db.compterArticlesPanier();
            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            Order order = new Order(date, nbArticles, total, "en_cours");
            db.ajouterCommande(order);
            db.viderPanier();
            runOnUiThread(() -> {
                chargerPanier();
                if (headerFragment != null) headerFragment.refresh();
                Toast.makeText(PanierActivity.this, R.string.commande_passee, Toast.LENGTH_SHORT).show();
                envoyerNotification();
            });
        });
    }

    private void envoyerNotification() {
        // Vérifier permission POST_NOTIFICATIONS (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_NOTIF);
                return;
            }
        }
        NotificationHelper.notifierCommande(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == REQ_NOTIF) {
            // Envoyer si accordé, ignorer proprement si refusé
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                NotificationHelper.notifierCommande(this);
            }
        }
    }
}
