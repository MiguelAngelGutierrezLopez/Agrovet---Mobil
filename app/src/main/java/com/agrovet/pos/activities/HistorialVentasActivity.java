package com.agrovet.pos.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.agrovet.pos.repositories.VentaRepository;
import com.agrovet.pos.viewmodels.VentaViewModel;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class HistorialVentasActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView rvVentas;
    private TextView txtBancoIngresos, txtBancoCantidad, txtBancoSubtipos;
    private TextView txtContadoIngresos, txtContadoCantidad;
    private TextView txtCreditoIngresos, txtCreditoCantidad, txtCreditoTotal, txtCreditoPendiente;
    private TextView btnFechaInicio, btnFechaFin;
    private Button btnFiltrarRango;
    private Button btnHoy, btnSemana, btnMes, btnAnio;

    private VentaViewModel viewModel;
    private com.agrovet.pos.viewmodels.MovimientoViewModel movimientoViewModel;
    private VentaAdapter adapter;
    private final List<Venta> ventasList = new ArrayList<>();
    private final List<com.agrovet.pos.models.Movimiento> movimientosList = new ArrayList<>();
    
    private String fechaInicioStr = "";
    private String fechaFinStr = "";
    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_ventas);

        viewModel = new ViewModelProvider(this).get(VentaViewModel.class);
        movimientoViewModel = new ViewModelProvider(this).get(com.agrovet.pos.viewmodels.MovimientoViewModel.class);

        initViews();
        setupToolbar();
        setupDrawer();
        setupRecyclerView();
        setupFilters();
        observeVentas();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvVentas = findViewById(R.id.rv_historial_ventas);
        
        txtBancoIngresos = findViewById(R.id.txt_banco_ingresos);
        txtBancoCantidad = findViewById(R.id.txt_banco_cantidad);
        txtBancoSubtipos = findViewById(R.id.txt_banco_subtipos);
        
        txtContadoIngresos = findViewById(R.id.txt_contado_ingresos);
        txtContadoCantidad = findViewById(R.id.txt_contado_cantidad);
        
        txtCreditoIngresos = findViewById(R.id.txt_credito_ingresos);
        txtCreditoCantidad = findViewById(R.id.txt_credito_cantidad);
        txtCreditoTotal = findViewById(R.id.txt_credito_total);
        txtCreditoPendiente = findViewById(R.id.txt_credito_pendiente);

        btnFechaInicio = findViewById(R.id.btn_fecha_inicio);
        btnFechaFin = findViewById(R.id.btn_fecha_fin);
        btnFiltrarRango = findViewById(R.id.btn_filtrar_rango);
        
        btnHoy = findViewById(R.id.btn_periodo_hoy);
        btnSemana = findViewById(R.id.btn_periodo_semana);
        btnMes = findViewById(R.id.btn_periodo_mes);
        btnAnio = findViewById(R.id.btn_periodo_anio);
    }

    private void setupFilters() {
        btnFechaInicio.setOnClickListener(v -> showDatePicker(true));
        btnFechaFin.setOnClickListener(v -> showDatePicker(false));
        btnFiltrarRango.setOnClickListener(v -> fetchFilteredVentas());

        btnHoy.setOnClickListener(v -> setPeriod(0));
        btnSemana.setOnClickListener(v -> setPeriod(7));
        btnMes.setOnClickListener(v -> setPeriod(30));
        btnAnio.setOnClickListener(v -> setPeriod(365));
    }

    private void showDatePicker(boolean isInicio) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            cal.set(year, month, dayOfMonth);
            String selected = apiDateFormat.format(cal.getTime());
            if (isInicio) {
                fechaInicioStr = selected;
                btnFechaInicio.setText(selected);
            } else {
                fechaFinStr = selected;
                btnFechaFin.setText(selected);
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setPeriod(int days) {
        Calendar cal = Calendar.getInstance();
        fechaFinStr = apiDateFormat.format(cal.getTime());
        if (days > 0) {
            cal.add(Calendar.DAY_OF_YEAR, -days);
            fechaInicioStr = apiDateFormat.format(cal.getTime());
        } else {
            fechaInicioStr = fechaFinStr;
        }
        btnFechaInicio.setText(fechaInicioStr);
        btnFechaFin.setText(fechaFinStr);
        fetchFilteredVentas();
    }

    private void fetchFilteredVentas() {
        if (fechaInicioStr.isEmpty() || fechaFinStr.isEmpty()) {
            Toast.makeText(this, "Seleccione ambas fechas", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Filtrando...", Toast.LENGTH_SHORT).show();
        viewModel.filtrarVentas(fechaInicioStr, fechaFinStr, null, null, new VentaRepository.FilterCallback() {
            @Override
            public void onResult(List<Venta> ventas) {
                runOnUiThread(() -> {
                    ventasList.clear();
                    ventasList.addAll(ventas);
                    adapter.notifyDataSetChanged();
                    updateStats(ventas);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(HistorialVentasActivity.this, message, Toast.LENGTH_LONG).show());
            }
        });
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            getSupportActionBar().setTitle("Historial de Ventas");
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

        movimientoViewModel.getAllMovimientos().observe(this, movimientos -> {
            if (movimientos != null) {
                movimientosList.clear();
                movimientosList.addAll(movimientos);
            }
        });
    }

    private void updateStats(List<Venta> ventas) {
        double bancoIngresos = 0, contadoIngresos = 0, creditoIngresos = 0;
        double creditoTotal = 0, creditoPendiente = 0;
        int bancoCant = 0, contadoCant = 0, creditoCant = 0;
        Set<String> bancoMetodos = new HashSet<>();

        for (Venta v : ventas) {
            String tipo = v.getTipoPago();
            if ("Banco".equalsIgnoreCase(tipo)) {
                bancoIngresos += v.getTotal();
                bancoCant++;
                if (v.getSubmetodoBanco() != null) bancoMetodos.add(v.getSubmetodoBanco());
            } else if ("Contado".equalsIgnoreCase(tipo) || "Efectivo".equalsIgnoreCase(tipo)) {
                contadoIngresos += v.getTotal();
                contadoCant++;
            } else if ("Crédito".equalsIgnoreCase(tipo)) {
                creditoIngresos += v.getAnticipo();
                creditoTotal += v.getTotal();
                creditoPendiente += (v.getTotal() - v.getAnticipo());
                creditoCant++;
            }
        }

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        
        if (txtBancoIngresos != null) txtBancoIngresos.setText(format.format(bancoIngresos));
        if (txtBancoCantidad != null) txtBancoCantidad.setText(String.valueOf(bancoCant));
        if (txtBancoSubtipos != null) {
            String metodos = bancoMetodos.isEmpty() ? "-" : String.join(", ", bancoMetodos);
            txtBancoSubtipos.setText("Métodos Bancarios: " + metodos);
        }

        if (txtContadoIngresos != null) txtContadoIngresos.setText(format.format(contadoIngresos));
        if (txtContadoCantidad != null) txtContadoCantidad.setText(String.valueOf(contadoCant));

        if (txtCreditoIngresos != null) txtCreditoIngresos.setText(format.format(creditoIngresos));
        if (txtCreditoCantidad != null) txtCreditoCantidad.setText(String.valueOf(creditoCant));
        if (txtCreditoTotal != null) txtCreditoTotal.setText(format.format(creditoTotal));
        if (txtCreditoPendiente != null) txtCreditoPendiente.setText(format.format(creditoPendiente));
    }

    private void mostrarDetalle(Venta venta) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_venta_detalle, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView txtTicket = view.findViewById(R.id.txt_detalle_ticket);
        TextView txtFecha = view.findViewById(R.id.txt_detalle_fecha);
        TextView txtCliente = view.findViewById(R.id.txt_detalle_cliente);
        TextView txtPago = view.findViewById(R.id.txt_detalle_pago);
        TextView txtTotal = view.findViewById(R.id.txt_detalle_total);
        RecyclerView rvItems = view.findViewById(R.id.rv_detalle_productos);
        android.widget.Button btnCerrar = view.findViewById(R.id.btn_detalle_cerrar);

        txtTicket.setText(getString(R.string.ticket_placeholder, venta.getTicket()));
        txtFecha.setText(getString(R.string.fecha_placeholder, venta.getFecha()));
        txtCliente.setText(getString(R.string.cliente_placeholder, venta.getNombreCliente()));
        
        String pagoInfo = venta.getTipoPago() + (venta.getSubmetodoBanco() != null ? " (" + venta.getSubmetodoBanco() + ")" : "");
        txtPago.setText(getString(R.string.pago_placeholder, pagoInfo));

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        txtTotal.setText(format.format(venta.getTotal()));

        rvItems.setLayoutManager(new LinearLayoutManager(this));
        
        viewModel.getItemsByVenta(venta.getId(), items -> {
            runOnUiThread(() -> {
                com.agrovet.pos.adapters.VentaItemAdapter itemAdapter = new com.agrovet.pos.adapters.VentaItemAdapter(items);
                rvItems.setAdapter(itemAdapter);
            });
        });

        btnCerrar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void confirmarEliminar(Venta venta) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Venta")
                .setMessage("¿Desea eliminar este registro? (También se eliminará de Caja)")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    // Eliminar movimiento de caja asociado
                    String razonBusqueda = "Venta " + ("Crédito".equalsIgnoreCase(venta.getTipoPago()) ? "a Crédito" : "Contado") + " - " + venta.getNombreCliente();
                    for (com.agrovet.pos.models.Movimiento m : movimientosList) {
                        if (m.getRazonIngreso() != null && m.getRazonIngreso().equals(razonBusqueda) && Math.abs(m.getIngresos() - (("Crédito".equalsIgnoreCase(venta.getTipoPago())) ? venta.getAnticipo() : venta.getTotal())) < 1) {
                            movimientoViewModel.deleteMovimiento(m);
                            break;
                        }
                    }
                    
                    viewModel.deleteVenta(venta.getId());
                    Toast.makeText(this, "Venta y movimiento eliminados", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
