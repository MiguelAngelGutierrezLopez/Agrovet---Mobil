package com.agrovet.pos.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.agrovet.pos.R;
import com.agrovet.pos.models.Venta;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class VentaAdapter extends RecyclerView.Adapter<VentaAdapter.ViewHolder> {

    private List<Venta> ventas;
    private OnVentaActionListener listener;

    public interface OnVentaActionListener {
        void onVerDetalle(Venta venta);
        void onEliminar(Venta venta);
    }

    public VentaAdapter(List<Venta> ventas, OnVentaActionListener listener) {
        this.ventas = ventas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_venta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Venta venta = ventas.get(position);
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

        holder.txtTicket.setText(venta.getTicket());
        holder.txtFecha.setText(venta.getFecha());
        holder.txtCliente.setText(venta.getCliente());
        holder.txtTotal.setText(format.format(venta.getTotal()));
        holder.txtMetodo.setText(venta.getMetodoPago());

        holder.btnVer.setOnClickListener(v -> listener.onVerDetalle(venta));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(venta));
    }

    @Override
    public int getItemCount() {
        return ventas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTicket, txtFecha, txtCliente, txtTotal, txtMetodo;
        Button btnVer, btnEliminar;

        ViewHolder(View itemView) {
            super(itemView);
            txtTicket = itemView.findViewById(R.id.txt_venta_ticket);
            txtFecha = itemView.findViewById(R.id.txt_venta_fecha);
            txtCliente = itemView.findViewById(R.id.txt_venta_cliente);
            txtTotal = itemView.findViewById(R.id.txt_venta_total);
            txtMetodo = itemView.findViewById(R.id.txt_venta_metodo);
            btnVer = itemView.findViewById(R.id.btn_venta_ver);
            btnEliminar = itemView.findViewById(R.id.btn_venta_eliminar);
        }
    }
}
