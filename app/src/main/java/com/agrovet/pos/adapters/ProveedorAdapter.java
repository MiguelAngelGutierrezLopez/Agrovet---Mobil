package com.agrovet.pos.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.agrovet.pos.R;
import com.agrovet.pos.models.Proveedor;
import java.util.List;

public class ProveedorAdapter extends RecyclerView.Adapter<ProveedorAdapter.ViewHolder> {

    private List<Proveedor> proveedores;
    private OnProveedorActionListener listener;

    public interface OnProveedorActionListener {
        void onEditar(Proveedor proveedor);
        void onEliminar(Proveedor proveedor);
    }

    public ProveedorAdapter(List<Proveedor> proveedores, OnProveedorActionListener listener) {
        this.proveedores = proveedores;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_proveedor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Proveedor proveedor = proveedores.get(position);

        holder.txtEmpresa.setText(proveedor.getNombreEmpresa());
        holder.txtNombreProveedor.setText("Contacto: " + proveedor.getNombreProveedor());
        holder.txtTelefono.setText("Tel: " + proveedor.getTelefono());
        
        // Forzar colores para visibilidad
        holder.txtEmpresa.setTextColor(holder.itemView.getContext().getColor(R.color.gris_oscuro));
        holder.txtNombreProveedor.setTextColor(holder.itemView.getContext().getColor(R.color.gris_medio));
        holder.txtTelefono.setTextColor(holder.itemView.getContext().getColor(R.color.teal));

        holder.txtEstado.setVisibility(View.GONE);

        if (proveedor.getProductos() != null && !proveedor.getProductos().isEmpty()) {
            holder.txtProductos.setText(proveedor.getProductos());
            holder.txtProductos.setVisibility(View.VISIBLE);
            holder.txtProductos.setTextColor(holder.itemView.getContext().getColor(R.color.gris_medio));
        } else {
            holder.txtProductos.setVisibility(View.GONE);
        }

        holder.btnEditar.setOnClickListener(v -> listener.onEditar(proveedor));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(proveedor));
    }

    @Override
    public int getItemCount() {
        return proveedores.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtEmpresa, txtNombreProveedor, txtTelefono, txtEstado, txtProductos;
        ImageButton btnEditar, btnEliminar;

        ViewHolder(View itemView) {
            super(itemView);
            txtEmpresa = itemView.findViewById(R.id.txt_empresa);
            txtNombreProveedor = itemView.findViewById(R.id.txt_nombre_proveedor);
            txtTelefono = itemView.findViewById(R.id.txt_telefono);
            txtEstado = itemView.findViewById(R.id.txt_estado);
            txtProductos = itemView.findViewById(R.id.txt_productos);
            btnEditar = itemView.findViewById(R.id.btn_editar);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);
        }
    }
}
