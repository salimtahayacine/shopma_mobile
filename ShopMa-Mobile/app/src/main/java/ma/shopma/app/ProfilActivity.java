package ma.shopma.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import ma.shopma.app.db.DatabaseHelper;
import ma.shopma.app.utils.PrefManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Profil utilisateur : photo (caméra), infos, SharedPreferences. */
public class ProfilActivity extends AppCompatActivity {

    public static final String EXTRA_USERNAME = "username";
    private static final int REQ_CAMERA = 100;
    private static final int REQ_IMAGE_CAPTURE = 200;

    private TextInputEditText etNom, etEmail, etAdresse;
    private ImageView ivPhoto;
    private PrefManager prefs;
    private String photoPath;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        username = getIntent().getStringExtra(EXTRA_USERNAME);
        if (username == null) username = "";
        prefs = new PrefManager(this);

        etNom = findViewById(R.id.et_nom);
        etEmail = findViewById(R.id.et_email_profil);
        etAdresse = findViewById(R.id.et_adresse);
        ivPhoto = findViewById(R.id.iv_photo);

        // Charger les données sauvegardées
        etNom.setText(prefs.getProfileName().isEmpty() ? username : prefs.getProfileName());
        etEmail.setText(prefs.getProfileEmail());
        etAdresse.setText(prefs.getProfileAddress());

        photoPath = prefs.getProfilePhoto();
        if (!photoPath.isEmpty()) {
            Glide.with(this).load(new File(photoPath)).into(ivPhoto);
        }

        // Bouton photo
        findViewById(R.id.btn_photo).setOnClickListener(v -> lancerCamera());

        // Sauvegarder
        findViewById(R.id.btn_sauvegarder).setOnClickListener(v -> sauvegarderProfil());

        // Historique commandes
        findViewById(R.id.btn_historique).setOnClickListener(v -> {
            Intent i = new Intent(this, CommandesActivity.class);
            i.putExtra(CommandesActivity.EXTRA_USERNAME, username);
            startActivity(i);
        });

        // Déconnexion
        findViewById(R.id.btn_deconnexion).setOnClickListener(v -> {
            prefs.clearSession();
            DatabaseHelper.getInstance(this).viderPanier();
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }

    private void lancerCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQ_CAMERA);
            return;
        }
        demarrerCapture();
    }

    private void demarrerCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) == null) return;

        File photoFile = null;
        try {
            String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            photoFile = File.createTempFile("PROFIL_" + stamp, ".jpg", dir);
            photoPath = photoFile.getAbsolutePath();
        } catch (IOException e) {
            return;
        }

        Uri photoUri = FileProvider.getUriForFile(this,
                getPackageName() + ".fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, REQ_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Glide.with(this).load(new File(photoPath)).into(ivPhoto);
            prefs.setProfilePhoto(photoPath);
        }
    }

    private void sauvegarderProfil() {
        String nom = etNom.getText() != null ? etNom.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String adresse = etAdresse.getText() != null ? etAdresse.getText().toString().trim() : "";
        prefs.setProfileName(nom);
        prefs.setProfileEmail(email);
        prefs.setProfileAddress(adresse);
        Toast.makeText(this, R.string.profil_sauvegarde, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int req, String[] perms, int[] results) {
        super.onRequestPermissionsResult(req, perms, results);
        if (req == REQ_CAMERA) {
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                demarrerCapture();
            } else {
                Toast.makeText(this, R.string.permission_camera_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
