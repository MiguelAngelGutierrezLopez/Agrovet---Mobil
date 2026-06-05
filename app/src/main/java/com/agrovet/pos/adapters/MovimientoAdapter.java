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

    public MovimientoAdapter(List<Movimiento> movimientos) {
        this.movimientos = movimientos;
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
    }

    @Override
    public int getItemCount() {
        return movimientos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtRazon, txtFecha, txtMonto;

        ViewHolder(View itemView) {
            super(itemView);
            txtRazon = itemView.findViewById(R.id.txt_movimiento_razon);
            txtFecha = itemView.findViewById(R.id.txt_movimiento_fecha);
            txtMonto = itemView.findViewById(R.id.txt_movimiento_monto);
        }
    }
}
