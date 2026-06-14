package com.agrovet.pos.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.agrovet.pos.R;
import com.agrovet.pos.models.Movimiento;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MovimientoAdapter extends RecyclerView.Adapter<MovimientoAdapter.ViewHolder> {

    private List<Movimiento> movimientos;
    private OnMovimientoActionListener listener;

    public interface OnMovimientoActionListener {
        void onEditar(Movimiento m);
        void onEliminar(Movimiento m);
    }

    public MovimientoAdapter(List<Movimiento> movimientos, OnMovimientoActionListener listener) {
        this.movimientos = movimientos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movimiento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movimiento movimiento = movimientos.get(position);
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

        holder.txtRazon.setText(movimiento.getRazon());
        holder.txtFecha.setText(movimiento.getFecha() + " • " + movimiento.getTipo());
        holder.txtMonto.setText(format.format(movimiento.getMonto()));

        if (movimiento.getTipo().equals("Egreso")) {
            holder.txtMonto.setTextColor(holder.itemView.getContext().getColor(R.color.rojo_error));
        } else {
            holder.txtMonto.setTextColor(holder.itemView.getContext().getColor(R.color.verde_exito));
        }

        // Restringir edición/eliminación si es venta
        if (holder.layoutAcciones != null) {
            if ("Venta de productos".equalsIgnoreCase(movimiento.getCategoria())) {
                holder.layoutAcciones.setVisibility(View.GONE);
            } else {
                holder.layoutAcciones.setVisibility(View.VISIBLE);
            }
        }

        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) listener.onEditar(movimiento);
        });
        
        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) listener.onEliminar(movimiento);
        });
    }

    @Override
    public int getItemCount() {
        return movimientos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtRazon, txtFecha, txtMonto;
        View btnEditar, btnEliminar, layoutAcciones;

        ViewHolder(View itemView) {
            super(itemView);
            txtRazon = itemView.findViewById(R.id.txt_movimiento_razon);
            txtFecha = itemView.findViewById(R.id.txt_movimiento_fecha);
            txtMonto = itemView.findViewById(R.id.txt_movimiento_monto);
            btnEditar = itemView.findViewById(R.id.btn_editar);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);
            layoutAcciones = itemView.findViewById(R.id.layout_acciones_movimiento);
        }
    }
}

