package com.agrovet.pos.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.agrovet.pos.R;
import com.agrovet.pos.models.VentaItem;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class VentaItemAdapter extends RecyclerView.Adapter<VentaItemAdapter.ViewHolder> {

    private final List<VentaItem> items;

    public VentaItemAdapter(List<VentaItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_venta_detalle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VentaItem item = items.get(position);
        holder.txtNombre.setText(item.getNombreProducto());
        
        String qtyText = "x" + item.getCantidad();
        holder.txtCantidad.setText(qtyText);
        
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        holder.txtTotal.setText(format.format(item.getTotal()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtCantidad, txtTotal;

        ViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txt_item_nombre);
            txtCantidad = itemView.findViewById(R.id.txt_item_cantidad);
            txtTotal = itemView.findViewById(R.id.txt_item_total);
        }
    }
}
