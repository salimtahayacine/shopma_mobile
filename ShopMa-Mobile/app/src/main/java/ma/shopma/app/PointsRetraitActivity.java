package ma.shopma.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Carte Google Maps avec ≥ 4 marqueurs de points de retrait au Maroc.
 * Gestion permission ACCESS_FINE_LOCATION.
 */
public class PointsRetraitActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQ_LOCATION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_retrait);

        // Demander la permission de localisation
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOCATION);
        }

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Centrer sur le Maroc
        LatLng maroc = new LatLng(31.7917, -7.0926);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(maroc, 5.5f));

        // Activer Ma position si permission accordée
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }

        // ≥ 4 marqueurs points de retrait (coordonnées Maroc)
        ajouterMarqueur(map, new LatLng(33.5731, -7.5898),
                "ShopMa Casablanca", "Bd Mohammed V, Casablanca");
        ajouterMarqueur(map, new LatLng(34.0209, -6.8416),
                "ShopMa Rabat", "Avenue Mohammed VI, Rabat");
        ajouterMarqueur(map, new LatLng(31.6295, -7.9811),
                "ShopMa Marrakech", "Place Djemaa el-Fna, Marrakech");
        ajouterMarqueur(map, new LatLng(35.7595, -5.8340),
                "ShopMa Tanger", "Bd Pasteur, Tanger");
        ajouterMarqueur(map, new LatLng(32.0099, -4.0065),
                "ShopMa Errachidia", "Avenue de l\'Istiqlal, Errachidia");
    }

    private void ajouterMarqueur(GoogleMap map, LatLng pos, String titre, String adresse) {
        map.addMarker(new MarkerOptions()
                .position(pos)
                .title(titre)
                .snippet(adresse));
    }

    @Override
    public void onRequestPermissionsResult(int req, String[] perms, int[] results) {
        super.onRequestPermissionsResult(req, perms, results);
        // Si refusée : la carte s'affiche quand même sans localisation → pas de crash
    }
}
