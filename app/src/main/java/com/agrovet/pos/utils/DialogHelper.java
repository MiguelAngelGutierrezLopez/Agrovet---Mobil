package com.agrovet.pos.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.agrovet.pos.R;
import com.agrovet.pos.models.Producto;
import com.agrovet.pos.models.Proveedor;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogHelper {

    public enum DialogType {
        SUCCESS, ERROR, INFO, WARNING
    }

    public interface OnProductoSaveListener {
        void onSave(Producto producto, boolean isEditing);
    }

    public static void showCustomAlert(Context context, DialogType type, String title, String message, Runnable onConfirm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_corporate, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView txtTitle = view.findViewById(R.id.dialog_title);
        TextView txtMessage = view.findViewById(R.id.dialog_message);
        Button btnConfirm = view.findViewById(R.id.btn_dialog_confirm);
        View indicator = view.findViewById(R.id.dialog_indicator);

        txtTitle.setText(title);
        txtMessage.setText(message);

        int color;
        switch (type) {
            case SUCCESS: color = context.getColor(R.color.verde_exito); break;
            case ERROR: color = context.getColor(R.color.rojo_error); break;
            case WARNING: color = context.getColor(R.color.mostaza); break;
            default: color = context.getColor(R.color.teal); break;
        }
        indicator.setBackgroundColor(color);

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            if (onConfirm != null) onConfirm.run();
        });

        dialog.show();
    }

    public static void mostrarDialogoProducto(Context context, LayoutInflater inflater, Producto producto, List<Proveedor> listaProv, OnProductoSaveListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = inflater.inflate(R.layout.dialog_producto, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        TextView tituloDialog = view.findViewById(R.id.titulo_dialog);
        EditText etCodigo = view.findViewById(R.id.et_codigo);
        TextInputEditText etNombre = view.findViewById(R.id.et_nombre);
        TextInputEditText etPrecio = view.findViewById(R.id.et_precio);
        TextInputEditText etStock = view.findViewById(R.id.et_stock);
        ChipGroup cgCategorias = view.findViewById(R.id.cg_categorias);
        TextInputEditText etPresentacion = view.findViewById(R.id.et_presentacion);
        AutoCompleteTextView etProveedor = view.findViewById(R.id.et_producto_proveedor);
        Button btnCancelar = view.findViewById(R.id.btn_cancelar);
        Button btnGuardar = view.findViewById(R.id.btn_guardar);

        final String[] CATEGORIAS = {
                "ANTIBIOTICOS", "BIOESTIMULANTES", "BIOLOGICOS", "COADYUVANTES", "CONCENTRADOS",
                "CONCENTRADO AVES PRODUCCION", "CONCENTRADOS GATOS", "CONCENTRADOS PERROS", "ENMIENDA",
                "FERTILIZANTES", "FUNGICIDAS", "HERBICIDAS", "INSECTICIDAS", "MAIZ", "MASCOTAS",
                "REGULADOR DE CRECIMIENTO", "REPUESTOS, BOMBAS Y ESTACIONARIAS", "SALES GANADERAS",
                "SEMILLAS", "VETERINARIA"
        };

        // Llenar Proveedores
        final Map<String, String> mapProveedores = new HashMap<>();
        if (listaProv != null) {
            List<String> nombres = new ArrayList<>();
            for (Proveedor prov : listaProv) {
                String label = prov.getNombreEmpresa() + " (" + prov.getNombreProveedor() + ")";
                nombres.add(label);
                mapProveedores.put(label, prov.getTelefono());
            }

            if (nombres.isEmpty()) {
                nombres.add("No hay registros");
            }

            ArrayAdapter<String> provAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, nombres);
            etProveedor.setAdapter(provAdapter);

            if (producto != null && producto.getProveedor() != null) {
                for (Proveedor prov : listaProv) {
                    if (prov.getTelefono().equals(producto.getProveedor())) {
                        String label = prov.getNombreEmpresa() + " (" + prov.getNombreProveedor() + ")";
                        etProveedor.setText(label, false);
                        break;
                    }
                }
            }
        }

        etProveedor.setOnClickListener(v -> etProveedor.showDropDown());

        // Llenar Chips de Categorias
        for (String cat : CATEGORIAS) {
            Chip chip = new Chip(context);
            chip.setText(cat);
            chip.setCheckable(true);
            cgCategorias.addView(chip);
            if (producto != null && cat.equalsIgnoreCase(producto.getCategoria())) {
                chip.setChecked(true);
            }
        }

        boolean isEditando = producto != null;

        if (isEditando) {
            tituloDialog.setText("Editar Producto");
            if (etCodigo != null) etCodigo.setText(String.valueOf(producto.getId()));
            etNombre.setText(producto.getNombre());
            etPrecio.setText(String.valueOf(producto.getPrecioVenta() != null ? producto.getPrecioVenta() : 0));
            etStock.setText(String.valueOf(producto.getCantidad() != null ? producto.getCantidad() : 0));
            etPresentacion.setText(producto.getPresentacion());
        } else {
            tituloDialog.setText("Nuevo Producto");
        }

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String precioStr = etPrecio.getText().toString().trim();
            String stockStr = etStock.getText().toString().trim();
            String presentacion = etPresentacion.getText().toString().trim();
            String nombreProv = etProveedor.getText().toString().trim();
            String telefonoProv = mapProveedores.get(nombreProv);

            int selectedChipId = cgCategorias.getCheckedChipId();

            if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty() || selectedChipId == -1) {
                Toast.makeText(context, "Nombre, precio, unidades y categoría son requeridos", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int precio = Integer.parseInt(precioStr);
                int unidades = Integer.parseInt(stockStr);
                String categoria = ((Chip) view.findViewById(selectedChipId)).getText().toString().toUpperCase();
                categoria = Normalizer.normalize(categoria, Normalizer.Form.NFD).replaceAll("\\p{M}", "");

                Producto targetProducto = isEditando ? producto : new Producto();
                targetProducto.setNombre(nombre);
                targetProducto.setPrecioVenta(precio);
                targetProducto.setCantidad(unidades);
                targetProducto.setCategoria(categoria);
                targetProducto.setPresentacion(presentacion);
                targetProducto.setProveedor(telefonoProv != null ? telefonoProv : "");
                
                if (!isEditando) {
                    targetProducto.setDescripcion("");
                    targetProducto.setPrecioCosto(0);
                    targetProducto.setSynced(false);
                }

                listener.onSave(targetProducto, isEditando);
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Precio y unidades deben ser numéricos", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
