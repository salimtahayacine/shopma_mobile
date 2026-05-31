package ma.shopma.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ma.shopma.app.R;
import ma.shopma.app.db.DatabaseHelper;

/**
 * HeaderFragment réutilisable dans Catalogue, Panier, Recherche, Commandes.
 * Affiche le nom de l'utilisateur + badge du nombre d'articles panier.
 */
public class HeaderFragment extends Fragment {

    public static final String ARG_USERNAME = "username";

    private TextView tvUsername;
    private TextView tvBadge;

    public static HeaderFragment newInstance(String username) {
        HeaderFragment f = new HeaderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_header, container, false);
        tvUsername = v.findViewById(R.id.tv_header_username);
        tvBadge = v.findViewById(R.id.tv_header_badge);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            String username = getArguments().getString(ARG_USERNAME, "");
            tvUsername.setText("Bonjour, " + username);
        }
        refresh();
    }

    /** Rafraîchit le badge en relisant le panier en base. */
    public void refresh() {
        if (getContext() == null) return;
        int count = DatabaseHelper.getInstance(getContext()).compterArticlesPanier();
        if (count > 0) {
            tvBadge.setText(String.valueOf(count));
            tvBadge.setVisibility(View.VISIBLE);
        } else {
            tvBadge.setVisibility(View.GONE);
        }
    }
}
