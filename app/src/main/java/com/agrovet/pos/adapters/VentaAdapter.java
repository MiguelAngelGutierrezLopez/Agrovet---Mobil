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

        // Datos principales del Item
        holder.txtTicket.setText(venta.getTicket() != null && !venta.getTicket().isEmpty() ? "#" + venta.getTicket() : "#---");
        holder.txtFecha.setText(venta.getFecha() != null ? venta.getFecha() : "--/--/----");
        holder.txtCliente.setText(venta.getCliente() != null && !venta.getCliente().isEmpty() ? venta.getCliente().toUpperCase() : "CONSUMIDOR FINAL");
        holder.txtTotal.setText(format.format(venta.getTotal()));
        
        // Ajuste de visualizacion para el metodo y estado
        String tipoPago = venta.getTipoPago();
        if (tipoPago != null && tipoPago.toLowerCase().contains("crédito")) {
            holder.txtMetodo.setText("CRÉDITO");
            holder.txtMetodo.setBackgroundResource(R.drawable.bg_stat_pendiente);
            holder.txtMetodo.setTextColor(holder.itemView.getContext().getColor(R.color.terracota));
        } else {
            String metodo = (venta.getMetodoPago() != null) ? venta.getMetodoPago().toUpperCase() : "CONTADO";
            holder.txtMetodo.setText(metodo);
            holder.txtMetodo.setBackgroundResource(R.drawable.bg_badge_contado);
            holder.txtMetodo.setTextColor(holder.itemView.getContext().getColor(R.color.verde_exito));
        }

        // --- Lógica de Panel Detallado (Expandible) ---
        // Al hacer click en el card o en "Ver Detalle", mostramos/ocultamos el panel detallado
        holder.btnVer.setOnClickListener(v -> {
            if (holder.layoutDetalle.getVisibility() == View.GONE) {
                // Llenar datos del detalle antes de mostrar
                holder.detTicket.setText(venta.getTicket());
                holder.detFecha.setText(venta.getFecha());
                holder.detCliente.setText(holder.txtCliente.getText());
                holder.detMetodo.setText(holder.txtMetodo.getText());
                holder.detMetodo.setTextColor(holder.txtMetodo.getTextColors());
                
                holder.detSubtotal.setText(format.format(venta.getSubtotal()));
                holder.detDescuento.setText(format.format(venta.getDescuento()));
                holder.detTotal.setText(format.format(venta.getTotal()));

                if ("CRÉDITO".equals(holder.txtMetodo.getText().toString())) {
                    holder.detSectionCredito.setVisibility(View.VISIBLE);
                    holder.detAnticipo.setText(format.format(venta.getAnticipo()));
                    holder.detSaldo.setText(format.format(venta.getTotal() - venta.getAnticipo()));
                } else {
                    holder.detSectionCredito.setVisibility(View.GONE);
                }

                holder.layoutDetalle.setVisibility(View.VISIBLE);
                holder.btnVer.setText("CERRAR DETALLE");
            } else {
                holder.layoutDetalle.setVisibility(View.GONE);
                holder.btnVer.setText("VER DETALLE");
            }
        });

        // El listener original sigue disponible si se necesita para un diálogo aparte
        // holder.btnVer.setOnClickListener(v -> listener.onVerDetalle(venta));

        holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(venta));
    }

    @Override
    public int getItemCount() {
        return ventas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTicket, txtFecha, txtCliente, txtTotal, txtMetodo;
        Button btnVer, btnEliminar;

        // Campos del panel detallado
        View layoutDetalle, detSectionCredito;
        TextView detTicket, detFecha, detCliente, detMetodo;
        TextView detSubtotal, detDescuento, detTotal;
        TextView detAnticipo, detSaldo;

        ViewHolder(View itemView) {
            super(itemView);
            txtTicket = itemView.findViewById(R.id.txt_venta_ticket);
            txtFecha = itemView.findViewById(R.id.txt_venta_fecha);
            txtCliente = itemView.findViewById(R.id.txt_venta_cliente);
            txtTotal = itemView.findViewById(R.id.txt_venta_total);
            txtMetodo = itemView.findViewById(R.id.txt_venta_metodo);
            btnVer = itemView.findViewById(R.id.btn_venta_ver);
            btnEliminar = itemView.findViewById(R.id.btn_venta_eliminar);

            // Vincular panel detallado
            layoutDetalle = itemView.findViewById(R.id.layout_detalle);
            detSectionCredito = itemView.findViewById(R.id.detalle_credito_section);
            detTicket = itemView.findViewById(R.id.detalle_ticket);
            detFecha = itemView.findViewById(R.id.detalle_fecha);
            detCliente = itemView.findViewById(R.id.detalle_cliente);
            detMetodo = itemView.findViewById(R.id.detalle_metodo);
            detSubtotal = itemView.findViewById(R.id.detalle_subtotal);
            detDescuento = itemView.findViewById(R.id.detalle_descuento);
            detTotal = itemView.findViewById(R.id.detalle_total);
            detAnticipo = itemView.findViewById(R.id.detalle_anticipo);
            detSaldo = itemView.findViewById(R.id.detalle_saldo);
        }
    }
}
