package ma.shopma.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ma.shopma.app.R;
import ma.shopma.app.model.Order;

import java.util.ArrayList;
import java.util.List;

/** Adapter BaseAdapter pour l'historique des commandes avec badge de statut coloré. */
public class OrderAdapter extends BaseAdapter {

    private final Context context;
    private List<Order> orders = new ArrayList<>();

    public OrderAdapter(Context context) { this.context = context; }

    public void setData(List<Order> data) {
        this.orders = data != null ? data : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override public int getCount() { return orders.size(); }
    @Override public Order getItem(int pos) { return orders.get(pos); }
    @Override public long getItemId(int pos) { return orders.get(pos).getId(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder h;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
            h = new ViewHolder();
            h.id = convertView.findViewById(R.id.tv_order_id);
            h.date = convertView.findViewById(R.id.tv_order_date);
            h.articles = convertView.findViewById(R.id.tv_order_articles);
            h.total = convertView.findViewById(R.id.tv_order_total);
            h.statut = convertView.findViewById(R.id.tv_order_statut);
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }

        Order o = getItem(position);
        h.id.setText("Commande #" + o.getId());
        h.date.setText(o.getDate());
        h.articles.setText(o.getNbArticles() + " article(s)");
        h.total.setText(String.format("%.2f MAD", o.getMontantTotal()));

        // Badge coloré selon le statut
        if ("livree".equals(o.getStatut())) {
            h.statut.setText("Livrée");
            h.statut.setBackground(context.getResources().getDrawable(R.drawable.bg_statut_livree, null));
            h.statut.setTextColor(context.getResources().getColor(R.color.statut_livree_text, null));
        } else {
            h.statut.setText("En cours");
            h.statut.setBackground(context.getResources().getDrawable(R.drawable.bg_statut_en_cours, null));
            h.statut.setTextColor(context.getResources().getColor(R.color.statut_en_cours_text, null));
        }

        return convertView;
    }

    static class ViewHolder {
        TextView id, date, articles, total, statut;
    }
}
