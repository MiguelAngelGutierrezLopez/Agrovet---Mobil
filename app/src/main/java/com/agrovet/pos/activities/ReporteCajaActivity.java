package com.agrovet.pos.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.agrovet.pos.R;
import com.agrovet.pos.adapters.MovimientoAdapter;
import com.agrovet.pos.models.Movimiento;
import com.agrovet.pos.utils.AppLogger;
import com.agrovet.pos.viewmodels.MovimientoViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReporteCajaActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView rvMovimientos;
    private TextView txtIngresos, txtEgresos, txtTotal;
    private MovimientoViewModel viewModel;
    private MovimientoAdapter adapter;
    private final List<Movimiento> movimientosList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_caja);

        viewModel = new ViewModelProvider(this).get(MovimientoViewModel.class);

        initViews();
        setupDrawer();
        setupRecyclerView();
        observeMovimientos();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvMovimientos = findViewById(R.id.rv_movimientos);
        txtIngresos = findViewById(R.id.txt_resumen_ingresos);
        txtEgresos = findViewById(R.id.txt_resumen_egresos);
        txtTotal = findViewById(R.id.txt_resumen_total);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add_movimiento);
        
        fabAdd.setOnClickListener(v -> mostrarDialogoMovimiento(null));
    }

    private void setupRecyclerView() {
        adapter = new MovimientoAdapter(movimientosList, new MovimientoAdapter.OnMovimientoActionListener() {
            @Override public void onEditar(Movimiento m) { mostrarDialogoMovimiento(m); }
            @Override public void onEliminar(Movimiento m) { confirmarEliminar(m); }
        });
        rvMovimientos.setLayoutManager(new LinearLayoutManager(this));
        rvMovimientos.setAdapter(adapter);
    }

    private void observeMovimientos() {
        viewModel.getAllMovimientos().observe(this, movimientos -> {
            if (movimientos != null) {
                movimientosList.clear();
                movimientosList.addAll(movimientos);
                adapter.notifyDataSetChanged();
                updateSummary(movimientosList);
            }
        });
    }

    private void updateSummary(List<Movimiento> movimientos) {
        double totalIngresos = 0;
        double totalEgresos = 0;
        
        for (Movimiento m : movimientos) {
            totalIngresos += (m.getIngresos() != null ? m.getIngresos() : 0.0);
            totalEgresos += (m.getEgresos() != null ? m.getEgresos() : 0.0);
        }
        
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        txtIngresos.setText(format.format(totalIngresos));
        txtEgresos.setText(format.format(totalEgresos));
        txtTotal.setText(format.format(totalIngresos - totalEgresos));
    }

    private void confirmarEliminar(Movimiento m) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Eliminar Movimiento")
                .setMessage("¿Desea eliminar este registro de caja?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    viewModel.deleteMovimiento(m);
                    Toast.makeText(this, "Movimiento eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoMovimiento(Movimiento movEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_nuevo_movimiento, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        TextInputEditText etRazon = view.findViewById(R.id.et_mov_razon);
        TextInputEditText etMonto = view.findViewById(R.id.et_mov_monto);
        AutoCompleteTextView etCategoria = view.findViewById(R.id.et_mov_categoria);
        TextInputEditText etFecha = view.findViewById(R.id.et_mov_fecha);
        RadioGroup rgTipo = view.findViewById(R.id.rg_mov_tipo);
        RadioButton rbIngreso = view.findViewById(R.id.rb_mov_ingreso);
        RadioButton rbEgreso = view.findViewById(R.id.rb_mov_egreso);
        Button btnGuardar = view.findViewById(R.id.btn_mov_guardar);
        Button btnCancelar = view.findViewById(R.id.btn_mov_cancelar);

        String[] categorias = {"Venta de productos", "Compra de insumos", "Pago de salarios", "Pago de alquiler", "Pago de auxiliares", "Pago de proveedores", "otro"};
        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categorias);
        etCategoria.setAdapter(adapterCat);

        if (movEdit != null) {
            etRazon.setText(movEdit.getRazon());
            etMonto.setText(String.valueOf(movEdit.getMonto()));
            etCategoria.setText(movEdit.getCategoria(), false);
            etFecha.setText(movEdit.getFecha().split(" ")[0]);
            if (movEdit.getIngresos() != null && movEdit.getIngresos() > 0) {
                rbIngreso.setChecked(true);
            } else {
                rbEgreso.setChecked(true);
            }
            btnGuardar.setText("ACTUALIZAR");
        }

        // Pre-llenar fecha actual si es nuevo
        if (movEdit == null) {
            String fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            etFecha.setText(fechaActual);
        }

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            try {
                String razon = etRazon.getText() != null ? etRazon.getText().toString().trim() : "";
                String montoStr = etMonto.getText() != null ? etMonto.getText().toString().trim() : "";
                String categoria = etCategoria.getText() != null ? etCategoria.getText().toString().trim() : "";
                String fecha = etFecha.getText() != null ? etFecha.getText().toString().trim() : "";
                
                if (razon.isEmpty() || montoStr.isEmpty() || categoria.isEmpty() || fecha.isEmpty()) {
                    Toast.makeText(this, "Complete todos los campos marcados con *", Toast.LENGTH_SHORT).show();
                    return;
                }

                double monto = Double.parseDouble(montoStr);
                String tipo = rgTipo.getCheckedRadioButtonId() == R.id.rb_mov_ingreso ? "ingreso" : "egreso";

                if ("otro".equalsIgnoreCase(categoria)) {
                    categoria = razon;
                }

                Movimiento m = (movEdit != null) ? movEdit : new Movimiento();
                m.setCategoria(categoria);
                m.setSynced(false);

                if ("ingreso".equals(tipo)) {
                    m.setIngresos(monto);
                    m.setRazonIngreso(razon);
                    m.setFechaIngreso(fecha + " 00:00:00");
                    m.setEgresos(0.0);
                    m.setRazonEgreso(null);
                    m.setFechaEgreso(null);
                } else {
                    m.setEgresos(monto);
                    m.setRazonEgreso(razon);
                    m.setFechaEgreso(fecha + " 00:00:00");
                    m.setIngresos(0.0);
                    m.setRazonIngreso(null);
                    m.setFechaIngreso(null);
                }

                if (movEdit != null) {
                    viewModel.updateMovimiento(m);
                    Toast.makeText(this, "Movimiento actualizado", Toast.LENGTH_SHORT).show();
                } else {
                    viewModel.addMovimiento(m);
                    Toast.makeText(this, "Movimiento registrado", Toast.LENGTH_SHORT).show();
                }
                
                dialog.dismiss();
            } catch (Exception e) {
                AppLogger.e("Error al registrar movimiento", e);
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
