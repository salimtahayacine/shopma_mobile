package ma.shopma.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import ma.shopma.app.R;
import ma.shopma.app.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter personnalisé (BaseAdapter) pour le catalogue et la recherche.
 * Implémente le recyclage de convertView.
 */
public class ProductAdapter extends BaseAdapter {

    public interface OnAddToCartListener {
        void onAddToCart(Product product);
    }

    private final Context context;
    private List<Product> products = new ArrayList<>();
    private OnAddToCartListener listener;

    public ProductAdapter(Context context) {
        this.context = context;
    }

    public void setOnAddToCartListener(OnAddToCartListener l) { this.listener = l; }

    public void setData(List<Product> data) {
        this.products = data != null ? data : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override public int getCount() { return products.size(); }
    @Override public Product getItem(int pos) { return products.get(pos); }
    @Override public long getItemId(int pos) { return products.get(pos).getId(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
            holder = new ViewHolder();
            holder.image = convertView.findViewById(R.id.iv_product_image);
            holder.title = convertView.findViewById(R.id.tv_product_title);
            holder.category = convertView.findViewById(R.id.tv_product_category);
            holder.price = convertView.findViewById(R.id.tv_product_price);
            holder.btnAdd = convertView.findViewById(R.id.btn_add_cart);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product p = getItem(position);
        holder.title.setText(p.getTitle());
        holder.category.setText(p.getCategory());
        holder.price.setText(String.format("%.2f MAD", p.getPrice()));

        Glide.with(context)
                .load(p.getImage())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(holder.image);

        holder.btnAdd.setOnClickListener(v -> {
            if (listener != null) listener.onAddToCart(p);
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView image;
        TextView title, category, price;
        Button btnAdd;
    }
}
