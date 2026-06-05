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
import com.agrovet.pos.viewmodels.MovimientoViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReporteCajaActivity extends AppCompatActivity {

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
        setupToolbar();
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
        
        fabAdd.setOnClickListener(v -> mostrarDialogoNuevoMovimiento());
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            getSupportActionBar().setTitle(R.string.titulo_reporte_caja);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new MovimientoAdapter(movimientosList);
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
            if ("Ingreso".equals(m.getTipo())) {
                totalIngresos += m.getMonto();
            } else {
                totalEgresos += m.getMonto();
            }
        }
        
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        txtIngresos.setText(format.format(totalIngresos));
        txtEgresos.setText(format.format(totalEgresos));
        txtTotal.setText(format.format(totalIngresos - totalEgresos));
    }

    private void mostrarDialogoNuevoMovimiento() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_nuevo_movimiento, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        TextInputEditText etRazon = view.findViewById(R.id.et_mov_razon);
        TextInputEditText etMonto = view.findViewById(R.id.et_mov_monto);
        RadioGroup rgTipo = view.findViewById(R.id.rg_mov_tipo);
        Button btnGuardar = view.findViewById(R.id.btn_mov_guardar);
        Button btnCancelar = view.findViewById(R.id.btn_mov_cancelar);

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            String razon = etRazon.getText().toString().trim();
            String montoStr = etMonto.getText().toString().trim();
            
            if (razon.isEmpty() || montoStr.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double monto = Double.parseDouble(montoStr);
                String tipo = rgTipo.getCheckedRadioButtonId() == R.id.rb_mov_ingreso ? "Ingreso" : "Egreso";
                String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

                viewModel.addMovimiento(new Movimiento(razon, fecha, monto, tipo));
                Toast.makeText(this, "Movimiento registrado", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Monto invalido", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
