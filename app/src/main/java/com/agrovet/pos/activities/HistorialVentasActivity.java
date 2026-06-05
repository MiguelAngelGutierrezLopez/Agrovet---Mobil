package com.agrovet.pos.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.agrovet.pos.R;
import com.agrovet.pos.adapters.VentaAdapter;
import com.agrovet.pos.models.Venta;
import com.agrovet.pos.viewmodels.VentaViewModel;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistorialVentasActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rvVentas;
    private TextView txtStatsVentas, txtStatsIngresos, txtStatsUtilidad;
    private VentaViewModel viewModel;
    private VentaAdapter adapter;
    private final List<Venta> ventasList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_ventas);

        viewModel = new ViewModelProvider(this).get(VentaViewModel.class);

        initViews();
        setupToolbar();
        setupRecyclerView();
        observeVentas();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvVentas = findViewById(R.id.rv_historial_ventas);
        txtStatsVentas = findViewById(R.id.txt_stats_ventas);
        txtStatsIngresos = findViewById(R.id.txt_stats_ingresos);
        txtStatsUtilidad = findViewById(R.id.txt_stats_utilidad);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            getSupportActionBar().setTitle(R.string.titulo_historial);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new VentaAdapter(ventasList, new VentaAdapter.OnVentaActionListener() {
            @Override
            public void onVerDetalle(Venta venta) {
                mostrarDetalle(venta);
            }

            @Override
            public void onEliminar(Venta venta) {
                confirmarEliminar(venta);
            }
        });
        rvVentas.setLayoutManager(new LinearLayoutManager(this));
        rvVentas.setAdapter(adapter);
    }

    private void observeVentas() {
        viewModel.getAllVentas().observe(this, ventas -> {
            if (ventas != null) {
                ventasList.clear();
                ventasList.addAll(ventas);
                adapter.notifyDataSetChanged();
                updateStats(ventas);
            }
        });
    }

    private void updateStats(List<Venta> ventas) {
        double totalIngresos = 0;
        for (Venta v : ventas) {
            totalIngresos += v.getTotal();
        }
        
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        txtStatsVentas.setText(String.valueOf(ventas.size()));
        txtStatsIngresos.setText(format.format(totalIngresos));
        txtStatsUtilidad.setText(format.format(totalIngresos * 0.3));
    }

    private void mostrarDetalle(Venta venta) {
        new AlertDialog.Builder(this)
                .setTitle("Detalle de Venta " + venta.getTicket())
                .setMessage("Cliente: " + venta.getCliente() + "\n" +
                           "Fecha: " + venta.getFecha() + "\n" +
                           "Metodo: " + venta.getMetodoPago() + "\n" +
                           "Total: " + venta.getTotal())
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private void confirmarEliminar(Venta venta) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.btn_eliminar)
                .setMessage(R.string.confirmar_eliminar_venta)
                .setPositiveButton("Si, Eliminar", (dialog, which) -> {
                    viewModel.deleteVenta(venta.getId());
                    Toast.makeText(this, "Venta eliminada", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
