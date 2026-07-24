package ma.shopma.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import ma.shopma.app.utils.NotificationHelper;
import ma.shopma.app.utils.PrefManager;

/**
 * Écran de connexion — design "Bienvenue sur ShopMa." (vert vif #22C55E).
 * - Validation : champs non vides, mot de passe >= 6 caractères.
 * - Identifiants en dur : youssef@shopma.ma / 123456
 * - Case "Se rappeler de moi" → email sauvegardé en SharedPreferences.
 */
public class LoginActivity extends AppCompatActivity {

    // Identifiants en dur (conformément au cahier des charges)
    private static final String VALID_EMAIL    = "youssef@shopma.ma";
    private static final String VALID_PASSWORD = "123456";

    private EditText etEmail, etPassword;
    private CheckBox cbRemember;
    private PrefManager prefs;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Canal de notification dès le lancement
        NotificationHelper.createChannel(this);

        prefs = new PrefManager(this);

        // Vues
        etEmail    = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        cbRemember = findViewById(R.id.cb_remember);
        Button btnLogin       = findViewById(R.id.btn_login);
        ImageView ivTogglePwd = findViewById(R.id.iv_toggle_password);
        TextView tvRegister   = findViewById(R.id.tv_register);

        // Titre "Bienvenue sur ShopMa." avec le "." en vert
        spannerTitre();

        // Restaurer l'email si "Se rappeler de moi" était coché
        if (prefs.isRememberMe()) {
            etEmail.setText(prefs.getSavedEmail());
            cbRemember.setChecked(true);
        }

        // Toggle visibilité mot de passe
        ivTogglePwd.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePwd.setAlpha(1f);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePwd.setAlpha(0.5f);
            }
            etPassword.setSelection(etPassword.getText() != null ? etPassword.getText().length() : 0);
        });

        btnLogin.setOnClickListener(v -> tentativeConnexion());

        // "Créer un compte" — pas encore implémenté, toast informatif
        tvRegister.setOnClickListener(v ->
            Toast.makeText(this, R.string.msg_inscription_bientot, Toast.LENGTH_SHORT).show()
        );
    }

    /** Colore le "." final du titre en vert via SpannableString. */
    private void spannerTitre() {
        TextView tvTitle = findViewById(R.id.tv_title);
        String raw = "Bienvenue sur\nShopMa.";
        SpannableString ss = new SpannableString(raw);
        int dotIndex = raw.lastIndexOf('.');
        int green = ContextCompat.getColor(this, R.color.primary);
        ss.setSpan(new ForegroundColorSpan(green), dotIndex, dotIndex + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTitle.setText(ss);
    }

    private void tentativeConnexion() {
        String email    = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        // Validation
        if (email.isEmpty()) {
            etEmail.setError(getString(R.string.error_field_empty));
            etEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError(getString(R.string.error_field_empty));
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etPassword.setError(getString(R.string.error_password_short));
            etPassword.requestFocus();
            return;
        }

        // Vérification des identifiants
        if (!email.equals(VALID_EMAIL) || !password.equals(VALID_PASSWORD)) {
            Toast.makeText(this, R.string.error_invalid_credentials, Toast.LENGTH_SHORT).show();
            return;
        }

        // Gestion "Se rappeler de moi"
        if (cbRemember.isChecked()) {
            prefs.setRememberMe(true);
            prefs.setSavedEmail(email);
        } else {
            prefs.setRememberMe(false);
            prefs.setSavedEmail("");
        }

        // Sauvegarder le nom utilisateur (partie avant @)
        String username = email.split("@")[0];
        prefs.setUsername(username);

        // Navigation vers le Dashboard
        Intent intent = new Intent(this, AccueilActivity.class);
        intent.putExtra(AccueilActivity.EXTRA_USERNAME, username);
        startActivity(intent);
        finish();
    }
}
