package com.agrovet.pos;

import com.agrovet.pos.activities.BaseActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import com.agrovet.pos.activities.SyncAlertActivity;
import android.os.Handler;
import android.os.Looper;
import com.agrovet.pos.activities.HistorialVentasActivity;
import com.agrovet.pos.activities.ProductosActivity;
import com.agrovet.pos.activities.ProveedoresActivity;
import com.agrovet.pos.activities.ReporteCajaActivity;
import com.agrovet.pos.activities.VentasActivity;
import com.agrovet.pos.models.Movimiento;
import com.agrovet.pos.models.Venta;
import com.agrovet.pos.utils.AppLogger;
import com.agrovet.pos.utils.SyncManager;
import com.agrovet.pos.viewmodels.ClienteViewModel;
import com.agrovet.pos.viewmodels.MovimientoViewModel;
import com.agrovet.pos.viewmodels.ProductoViewModel;
import com.agrovet.pos.viewmodels.VentaViewModel;
import android.widget.LinearLayout;
import com.google.android.material.navigation.NavigationView;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends BaseActivity {

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView txtTotalClientes, txtTotalProductos, txtVentasHoy, txtCajaSaldo, txtDbStatus;
    private View syncIndicator;
    private com.github.mikephil.charting.charts.PieChart chartMetodos;
    private CardView cardClientes, cardProductos, cardVentasHoy, cardReporteCaja;
    private Button btnSyncBatch;

    private ClienteViewModel clienteViewModel;
    private ProductoViewModel productoViewModel;
    private VentaViewModel ventaViewModel;
    private MovimientoViewModel movimientoViewModel;

    private final List<Venta> lastVentas = new ArrayList<>();
    private final List<Movimiento> lastMovimientos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setupPermissionLauncher();
        askNotificationPermission();

        initViews();
        setupDrawer();
        setupClickListeners();

        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
        productoViewModel = new ViewModelProvider(this).get(ProductoViewModel.class);
        ventaViewModel = new ViewModelProvider(this).get(VentaViewModel.class);
        movimientoViewModel = new ViewModelProvider(this).get(MovimientoViewModel.class);

        loadDashboardData();
        setupBackPressed();
        
        handleSyncIntent();
        startChangeObserver();
        
        AppLogger.i("MainActivity lista");
    }

    private void handleSyncIntent() {
        if (getIntent().getBooleanExtra("perform_sync_pull", false)) {
            new Handler(Looper.getMainLooper()).postDelayed(this::showSyncDashboardManual, 500);
        }
    }

    private void startChangeObserver() {
        // Ejecutar cada minuto
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                new SyncManager(MainActivity.this).checkForServerChanges(new SyncManager.SyncCallback() {
                    @Override
                    public void onSuccess(String modulo) {
                        runOnUiThread(() -> {
                            if (syncIndicator != null) syncIndicator.setVisibility(View.VISIBLE);
                        });
                    }
                    @Override public void onError(String message) {}
                    @Override public void onProgress(String status) {}
                });
                new Handler(Looper.getMainLooper()).postDelayed(this, 60000);
            }
        }, 30000); // Primera ejecución a los 30s
    }

    private void setupPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                AppLogger.i("Permiso de notificaciones concedido");
            } else {
                AppLogger.w("Permiso de notificaciones denegado");
            }
        });
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        
        txtTotalClientes = findViewById(R.id.txt_total_clientes);
        txtTotalProductos = findViewById(R.id.txt_total_productos);
        txtVentasHoy = findViewById(R.id.txt_ventas_hoy);
        txtCajaSaldo = findViewById(R.id.txt_caja_saldo);
        txtDbStatus = findViewById(R.id.txt_db_status);
        syncIndicator = findViewById(R.id.indicator_sync_needed);
        
        cardClientes = findViewById(R.id.card_clientes);
        cardProductos = findViewById(R.id.card_productos);
        cardVentasHoy = findViewById(R.id.card_ventas_hoy);
        cardReporteCaja = findViewById(R.id.card_reporte_caja);
        
        chartMetodos = findViewById(R.id.chart_pie_metodos);
        
        btnSyncBatch = findViewById(R.id.btn_sync_to_web);
        if (btnSyncBatch != null) {
            btnSyncBatch.setOnClickListener(v -> showSyncDashboard());
        }

        View btnManualReboot = findViewById(R.id.btn_manual_reboot);
        if (btnManualReboot != null) {
            btnManualReboot.setOnClickListener(v -> showSyncDashboardManual());
        }
        
        findViewById(R.id.fab_view_logs).setOnClickListener(v -> {
            startActivity(new Intent(this, com.agrovet.pos.activities.LogViewerActivity.class));
        });
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupClickListeners() {
        // Los botones de acceso directo han sido removidos del layout
    }

    private void loadDashboardData() {
        clienteViewModel.getClientes().observe(this, clientes -> {
            if (clientes != null) txtTotalClientes.setText(String.valueOf(clientes.size()));
        });

        productoViewModel.getProductos().observe(this, productos -> {
            if (productos != null) txtTotalProductos.setText(String.valueOf(productos.size()));
        });

        ventaViewModel.getAllVentas().observe(this, ventas -> {
            if (ventas != null) {
                lastVentas.clear();
                lastVentas.addAll(ventas);
                txtVentasHoy.setText(String.valueOf(ventas.size()));
                updateCajaTotal();
                updateChart(ventas);
            }
        });

        movimientoViewModel.getAllMovimientos().observe(this, movimientos -> {
            if (movimientos != null) {
                lastMovimientos.clear();
                lastMovimientos.addAll(movimientos);
                updateCajaTotal();
            }
        });
    }

    private void updateChart(List<Venta> ventas) {
        if (chartMetodos == null || ventas == null || ventas.isEmpty()) return;

        Map<String, Double> salesByMethod = new HashMap<>();
        for (Venta v : ventas) {
            String method = v.getTipoPago();
            if (method == null) method = "Otro";
            salesByMethod.put(method, salesByMethod.getOrDefault(method, 0.0) + v.getTotal());
        }

        List<com.github.mikephil.charting.data.PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : salesByMethod.entrySet()) {
            entries.add(new com.github.mikephil.charting.data.PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        com.github.mikephil.charting.data.PieDataSet dataSet = new com.github.mikephil.charting.data.PieDataSet(entries, "");
        
        // Colores temáticos de Agrovet
        int[] colors = {
                getResources().getColor(R.color.teal),
                getResources().getColor(R.color.mostaza),
                getResources().getColor(R.color.terracota),
                getResources().getColor(R.color.turquesa)
        };
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(getResources().getColor(R.color.white));

        com.github.mikephil.charting.data.PieData pieData = new com.github.mikephil.charting.data.PieData(dataSet);
        chartMetodos.setData(pieData);
        
        chartMetodos.setUsePercentValues(true);
        chartMetodos.getDescription().setEnabled(false);
        chartMetodos.setDrawHoleEnabled(true);
        chartMetodos.setHoleColor(android.R.color.transparent);
        chartMetodos.setCenterText("Métodos");
        chartMetodos.setCenterTextSize(14f);
        chartMetodos.setEntryLabelColor(getResources().getColor(R.color.gris_oscuro));
        chartMetodos.getLegend().setEnabled(true);
        chartMetodos.getLegend().setVerticalAlignment(com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM);
        chartMetodos.getLegend().setHorizontalAlignment(com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER);
        
        chartMetodos.animateY(1000);
        chartMetodos.invalidate();
    }

    private void updateCajaTotal() {
        // El usuario pide que SALDO CAJA sea igual al TOTAL en el reporte de caja.
        // El reporte de caja calcula: ingresos - egresos.
        // No incluye las ventas directamente a menos que se registren como ingresos.
        double totalIngresos = 0;
        double totalEgresos = 0;
        
        for (Movimiento m : lastMovimientos) {
            totalIngresos += (m.getIngresos() != null ? m.getIngresos() : 0);
            totalEgresos += (m.getEgresos() != null ? m.getEgresos() : 0);
        }
        
        double saldo = totalIngresos - totalEgresos;
        
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        txtCajaSaldo.setText(format.format(saldo));
    }

    private void showSyncDashboardManual() {
        ProgressBar mainProgressBar = findViewById(R.id.progress_main_sync);
        if (mainProgressBar != null) {
            mainProgressBar.setVisibility(View.VISIBLE);
        }
        
        SyncManager syncManager = new SyncManager(this);
        syncManager.pullNewData(new SyncManager.SyncCallback() {
            @Override
            public void onSuccess(String summary) {
                runOnUiThread(() -> {
                    if (mainProgressBar != null) mainProgressBar.setVisibility(View.GONE);
                    if (syncIndicator != null) syncIndicator.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, summary, Toast.LENGTH_LONG).show();
                    loadDashboardData();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    if (mainProgressBar != null) mainProgressBar.setVisibility(View.GONE);
                    com.agrovet.pos.utils.DialogHelper.showCustomAlert(
                            MainActivity.this,
                            com.agrovet.pos.utils.DialogHelper.DialogType.ERROR,
                            getString(R.string.sync_error_title),
                            message,
                            null
                    );
                });
            }

            @Override
            public void onProgress(String status) {
                // Opcional: podrías mostrar el progreso en un Toast o TextView pequeño
            }
        });
    }

    private void showSyncDashboard() {
        showSyncDashboardWithAction(false);
    }

    private void showSyncDashboardWithAction(boolean autoPull) {
        View view = getLayoutInflater().inflate(R.layout.dialog_sync_dashboard, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(true)
                .create();

        TextView txtSummary = view.findViewById(R.id.txt_sync_summary);
        TextView txtStatus = view.findViewById(R.id.txt_sync_status);
        ProgressBar progressBar = view.findViewById(R.id.progress_sync);
        Button btnCorregir = view.findViewById(R.id.btn_corregir_formatos);
        Button btnEnviar = view.findViewById(R.id.btn_enviar_datos);

        SyncManager syncManager = new SyncManager(this);

        syncManager.getSyncSummary(new SyncManager.SyncCallback() {
            @Override
            public void onSuccess(String summary) {
                runOnUiThread(() -> txtSummary.setText(summary));
            }
            @Override public void onError(String message) {}
            @Override public void onProgress(String status) {}
        });

        Runnable performPull = () -> {
            btnEnviar.setEnabled(false);
            btnCorregir.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            txtStatus.setVisibility(View.VISIBLE);
            
            syncManager.pullNewData(new SyncManager.SyncCallback() {
                @Override
                public void onSuccess(String summary) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        txtStatus.setText(summary);
                        btnEnviar.setEnabled(true);
                        btnCorregir.setEnabled(true);
                        if (syncIndicator != null) syncIndicator.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Base de datos actualizada", Toast.LENGTH_SHORT).show();
                        loadDashboardData();
                    });
                }
                @Override
                public void onError(String message) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        txtStatus.setText(message);
                        btnEnviar.setEnabled(true);
                        btnCorregir.setEnabled(true);
                        com.agrovet.pos.utils.DialogHelper.showCustomAlert(
                                MainActivity.this,
                                com.agrovet.pos.utils.DialogHelper.DialogType.ERROR,
                                getString(R.string.sync_error_title),
                                message,
                                null
                        );
                    });
                }
                @Override
                public void onProgress(String status) {
                    runOnUiThread(() -> txtStatus.setText(status));
                }
            });
        };

        if (autoPull) {
            new Handler(Looper.getMainLooper()).postDelayed(performPull, 500);
        }

        btnCorregir.setOnClickListener(v -> {
            btnCorregir.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            txtStatus.setVisibility(View.VISIBLE);
            
            syncManager.correctFormats(new SyncManager.SyncCallback() {
                @Override
                public void onSuccess(String summary) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        txtStatus.setText(summary);
                        btnCorregir.setEnabled(true);
                    });
                }
                @Override
                public void onError(String message) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        txtStatus.setText(message);
                        btnCorregir.setEnabled(true);
                    });
                }
                @Override
                public void onProgress(String status) {
                    runOnUiThread(() -> txtStatus.setText(status));
                }
            });
        });

        btnEnviar.setOnClickListener(v -> {
            btnEnviar.setEnabled(false);
            btnCorregir.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            txtStatus.setVisibility(View.VISIBLE);

            syncManager.sendDataToWeb(new SyncManager.SyncCallback() {
                @Override
                public void onSuccess(String summary) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        txtStatus.setText(summary);
                        Toast.makeText(MainActivity.this, summary, Toast.LENGTH_SHORT).show();
                        loadDashboardData(); // Refresh main screen counts
                        dialog.dismiss();
                    });
                }
                @Override
                public void onError(String message) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        txtStatus.setText(message);
                        btnEnviar.setEnabled(true);
                        btnCorregir.setEnabled(true);
                        com.agrovet.pos.utils.DialogHelper.showCustomAlert(
                                MainActivity.this,
                                com.agrovet.pos.utils.DialogHelper.DialogType.ERROR,
                                getString(R.string.sync_error_title),
                                message,
                                null
                        );
                    });
                }
                @Override
                public void onProgress(String status) {
                    runOnUiThread(() -> txtStatus.setText(status));
                }
            });
        });

        dialog.show();
    }

    private void openProveedoresActivity() {
        startActivity(new Intent(this, ProveedoresActivity.class));
    }

    private void openProductosActivity() {
        startActivity(new Intent(this, ProductosActivity.class));
    }

    private void openVentasActivity() {
        startActivity(new Intent(this, VentasActivity.class));
    }

    private void openHistorialVentasActivity() {
        startActivity(new Intent(this, HistorialVentasActivity.class));
    }

    private void openReporteCajaActivity() {
        startActivity(new Intent(this, ReporteCajaActivity.class));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_inicio) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_proveedores) {
            openProveedoresActivity();
        } else if (id == R.id.nav_productos) {
            openProductosActivity();
        } else if (id == R.id.nav_ventas) {
            openVentasActivity();
        } else if (id == R.id.nav_historial) {
            openHistorialVentasActivity();
        } else if (id == R.id.nav_reporte_caja) {
            openReporteCajaActivity();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
