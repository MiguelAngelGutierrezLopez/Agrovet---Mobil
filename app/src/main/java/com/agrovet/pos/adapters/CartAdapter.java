package com.agrovet.pos.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.agrovet.pos.R;
import com.agrovet.pos.models.CartItem;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> cartItems;
    private OnCartActionListener listener;

    public interface OnCartActionListener {
        void onRemove(CartItem item);
    }

    public CartAdapter(List<CartItem> cartItems, OnCartActionListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

        holder.txtNombre.setText(item.getProducto().getNombre());
        holder.txtCantidad.setText(String.valueOf(item.getCantidad()));
        holder.txtTotal.setText(format.format(item.getTotal()));

        holder.btnRemove.setOnClickListener(v -> listener.onRemove(item));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtCantidad, txtTotal;
        ImageButton btnRemove;

        ViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txt_cart_nombre);
            txtCantidad = itemView.findViewById(R.id.txt_cart_cantidad);
            txtTotal = itemView.findViewById(R.id.txt_cart_total);
            btnRemove = itemView.findViewById(R.id.btn_cart_remove);
        }
    }
}
