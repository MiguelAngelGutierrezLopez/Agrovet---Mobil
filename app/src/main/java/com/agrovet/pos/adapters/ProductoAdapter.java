package com.agrovet.pos.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.agrovet.pos.R;
import com.agrovet.pos.models.Producto;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private List<Producto> productos;
    private OnProductoActionListener listener;

    public interface OnProductoActionListener {
        void onEditar(Producto producto);
        void onEliminar(Producto producto);
    }

    public ProductoAdapter(List<Producto> productos, OnProductoActionListener listener) {
        this.productos = productos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto producto = productos.get(position);

        NumberFormat formatPeso = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

        holder.txtNombre.setText(producto.getNombre());
        holder.txtCodigo.setText("Código: " + producto.getCodigo());
        holder.txtPrecio.setText(formatPeso.format(producto.getPrecioVenta()));
        holder.txtStock.setText("📦 Unidades: " + producto.getUnidades());

        if (producto.getUnidades() <= 5) {
            holder.txtStock.setTextColor(holder.itemView.getContext().getColor(R.color.rojo_error));
        } else if (producto.getUnidades() <= 15) {
            holder.txtStock.setTextColor(holder.itemView.getContext().getColor(R.color.mostaza));
        } else {
            holder.txtStock.setTextColor(holder.itemView.getContext().getColor(R.color.gris_medio));
        }

        if (producto.getPresentacion() != null && !producto.getPresentacion().isEmpty()) {
            holder.txtCategoria.setText("Presentación: " + producto.getPresentacion());
            holder.txtCategoria.setVisibility(View.VISIBLE);
        } else {
            holder.txtCategoria.setVisibility(View.GONE);
        }

        holder.btnEditar.setOnClickListener(v -> listener.onEditar(producto));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(producto));
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtCodigo, txtPrecio, txtStock, txtCategoria;
        ImageButton btnEditar, btnEliminar;

        ViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txt_nombre);
            txtCodigo = itemView.findViewById(R.id.txt_codigo);
            txtPrecio = itemView.findViewById(R.id.txt_precio);
            txtStock = itemView.findViewById(R.id.txt_stock);
            txtCategoria = itemView.findViewById(R.id.txt_categoria);
            btnEditar = itemView.findViewById(R.id.btn_editar);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);
        }
    }
}
