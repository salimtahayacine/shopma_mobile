package ma.shopma.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/** Wrapper SharedPreferences pour la session, le login et le profil. */
public class PrefManager {

    private static final String PREFS_NAME = "shopma_prefs";

    private static final String KEY_REMEMBER = "remember_me";
    private static final String KEY_SAVED_EMAIL = "saved_email";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PROFILE_NAME = "profile_name";
    private static final String KEY_PROFILE_EMAIL = "profile_email";
    private static final String KEY_PROFILE_ADDRESS = "profile_address";
    private static final String KEY_PROFILE_PHOTO = "profile_photo";

    private final SharedPreferences prefs;

    public PrefManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // --- Remember me ---
    public void setRememberMe(boolean value) { prefs.edit().putBoolean(KEY_REMEMBER, value).apply(); }
    public boolean isRememberMe() { return prefs.getBoolean(KEY_REMEMBER, false); }

    public void setSavedEmail(String email) { prefs.edit().putString(KEY_SAVED_EMAIL, email).apply(); }
    public String getSavedEmail() { return prefs.getString(KEY_SAVED_EMAIL, ""); }

    // --- Session ---
    public void setUsername(String name) { prefs.edit().putString(KEY_USERNAME, name).apply(); }
    public String getUsername() { return prefs.getString(KEY_USERNAME, ""); }

    // --- Profil ---
    public void setProfileName(String v) { prefs.edit().putString(KEY_PROFILE_NAME, v).apply(); }
    public String getProfileName() { return prefs.getString(KEY_PROFILE_NAME, ""); }

    public void setProfileEmail(String v) { prefs.edit().putString(KEY_PROFILE_EMAIL, v).apply(); }
    public String getProfileEmail() { return prefs.getString(KEY_PROFILE_EMAIL, ""); }

    public void setProfileAddress(String v) { prefs.edit().putString(KEY_PROFILE_ADDRESS, v).apply(); }
    public String getProfileAddress() { return prefs.getString(KEY_PROFILE_ADDRESS, ""); }

    public void setProfilePhoto(String path) { prefs.edit().putString(KEY_PROFILE_PHOTO, path).apply(); }
    public String getProfilePhoto() { return prefs.getString(KEY_PROFILE_PHOTO, ""); }

    /** Efface toutes les données de session (déconnexion). */
    public void clearSession() {
        prefs.edit()
                .remove(KEY_USERNAME)
                .remove(KEY_PROFILE_NAME)
                .remove(KEY_PROFILE_EMAIL)
                .remove(KEY_PROFILE_ADDRESS)
                .remove(KEY_PROFILE_PHOTO)
                .apply();
    }
}
