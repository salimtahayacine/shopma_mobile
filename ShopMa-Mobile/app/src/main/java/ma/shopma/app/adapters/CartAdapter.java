package ma.shopma.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import ma.shopma.app.R;
import ma.shopma.app.model.CartItem;

import java.util.ArrayList;
import java.util.List;

/** Adapter BaseAdapter pour la liste du panier avec gestion des quantités. */
public class CartAdapter extends BaseAdapter {

    public interface OnQuantityChangeListener {
        void onChange(CartItem item, int newQty);
    }

    private final Context context;
    private List<CartItem> items = new ArrayList<>();
    private OnQuantityChangeListener listener;

    public CartAdapter(Context context) { this.context = context; }

    public void setOnQuantityChangeListener(OnQuantityChangeListener l) { this.listener = l; }

    public void setData(List<CartItem> data) {
        this.items = data != null ? data : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override public int getCount() { return items.size(); }
    @Override public CartItem getItem(int pos) { return items.get(pos); }
    @Override public long getItemId(int pos) { return items.get(pos).getId(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder h;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
            h = new ViewHolder();
            h.title = convertView.findViewById(R.id.tv_cart_title);
            h.price = convertView.findViewById(R.id.tv_cart_price);
            h.qty = convertView.findViewById(R.id.tv_qty);
            h.btnMinus = convertView.findViewById(R.id.btn_minus);
            h.btnPlus = convertView.findViewById(R.id.btn_plus);
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }

        CartItem item = getItem(position);
        h.title.setText(item.getTitle());
        h.price.setText(String.format("%.2f MAD", item.getSousTotal()));
        h.qty.setText(String.valueOf(item.getQuantity()));

        h.btnMinus.setOnClickListener(v -> {
            if (listener != null) listener.onChange(item, item.getQuantity() - 1);
        });
        h.btnPlus.setOnClickListener(v -> {
            if (listener != null) listener.onChange(item, item.getQuantity() + 1);
        });

        return convertView;
    }

    static class ViewHolder {
        TextView title, price, qty;
        Button btnMinus, btnPlus;
    }
}
