package com.agrovet.pos.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.agrovet.pos.R;
import com.agrovet.pos.models.Cliente;
import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ViewHolder> {

    private List<Cliente> clientes;
    private OnClienteActionListener listener;

    public interface OnClienteActionListener {
        void onEditar(Cliente cliente);
        void onEliminar(Cliente cliente);
    }

    public ClienteAdapter(List<Cliente> clientes, OnClienteActionListener listener) {
        this.clientes = clientes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cliente, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cliente cliente = clientes.get(position);

        holder.txtNombre.setText(cliente.getNombre());
        holder.txtCedula.setText("Cédula: " + cliente.getCedula());

        if (String.valueOf(cliente.getTelefono()) != null && cliente.getTelefono() != 0) {
            holder.txtTelefono.setText("📞 " + cliente.getTelefono());
            holder.txtTelefono.setVisibility(View.VISIBLE);
        } else {
            holder.txtTelefono.setVisibility(View.GONE);
        }

        if (cliente.getCorreo() != null && !cliente.getCorreo().isEmpty()) {
            holder.txtCorreo.setText("✉️ " + cliente.getCorreo());
            holder.txtCorreo.setVisibility(View.VISIBLE);
        } else {
            holder.txtCorreo.setVisibility(View.GONE);
        }

        holder.txtDireccion.setText("Deuda: $" + cliente.getDeuda());
        holder.txtDireccion.setVisibility(View.VISIBLE);

        holder.btnEditar.setOnClickListener(v -> listener.onEditar(cliente));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(cliente));
    }

    @Override
    public int getItemCount() {
        return clientes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtCedula, txtTelefono, txtCorreo, txtDireccion;
        ImageButton btnEditar, btnEliminar;

        ViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txt_nombre);
            txtCedula = itemView.findViewById(R.id.txt_cedula);
            txtTelefono = itemView.findViewById(R.id.txt_telefono);
            txtCorreo = itemView.findViewById(R.id.txt_correo);
            txtDireccion = itemView.findViewById(R.id.txt_direccion);
            btnEditar = itemView.findViewById(R.id.btn_editar);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);
        }
    }
}
