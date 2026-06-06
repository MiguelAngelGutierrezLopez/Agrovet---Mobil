package com.agrovet.pos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import com.agrovet.pos.activities.ClientesActivity;
import com.agrovet.pos.activities.HistorialVentasActivity;
import com.agrovet.pos.activities.ProductosActivity;
import com.agrovet.pos.activities.ProveedoresActivity;
import com.agrovet.pos.activities.ReporteCajaActivity;
import com.agrovet.pos.activities.VentasActivity;
import com.agrovet.pos.models.Movimiento;
import com.agrovet.pos.models.Venta;
import com.agrovet.pos.utils.AppLogger;
import com.agrovet.pos.viewmodels.ClienteViewModel;
import com.agrovet.pos.viewmodels.MovimientoViewModel;
import com.agrovet.pos.viewmodels.ProductoViewModel;
import com.agrovet.pos.viewmodels.VentaViewModel;
import com.google.android.material.navigation.NavigationView;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView txtTotalClientes, txtTotalProductos, txtVentasHoy, txtCajaSaldo, txtDbStatus;
    private CardView cardClientes, cardProductos, cardVentasHoy, cardReporteCaja;
    private CardView btnClientes, btnProveedores, btnProductos, btnVentas, btnStats;
    
    private ClienteViewModel clienteViewModel;
    private ProductoViewModel productoViewModel;
    private VentaViewModel ventaViewModel;
    private MovimientoViewModel movimientoViewModel;

    private List<Venta> lastVentas = new ArrayList<>();
    private List<Movimiento> lastMovimientos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupPermissionLauncher();
        setContentView(R.layout.activity_main);

        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
        productoViewModel = new ViewModelProvider(this).get(ProductoViewModel.class);
        ventaViewModel = new ViewModelProvider(this).get(VentaViewModel.class);
        movimientoViewModel = new ViewModelProvider(this).get(MovimientoViewModel.class);

        initViews();
        setupToolbar();
        setupNavigationDrawer();
        setupClickListeners();
        loadDashboardData();
        setupBackPressed();
        askNotificationPermission();
        
        AppLogger.i("MainActivity lista");
    }

    private void setupPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) AppLogger.i("Permiso de notificaciones concedido");
                    else AppLogger.w("Permiso de notificaciones denegado");
                }
        );
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
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
        
        cardClientes = findViewById(R.id.card_clientes);
        cardProductos = findViewById(R.id.card_productos);
        cardVentasHoy = findViewById(R.id.card_ventas_hoy);
        cardReporteCaja = findViewById(R.id.card_reporte_caja);
        
        btnClientes = findViewById(R.id.btn_clientes);
        btnProveedores = findViewById(R.id.btn_proveedores);
        btnProductos = findViewById(R.id.btn_productos);
        btnVentas = findViewById(R.id.btn_ventas);
        btnStats = findViewById(R.id.btn_stats);
        
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
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupClickListeners() {
        cardClientes.setOnClickListener(v -> openClientesActivity());
        cardProductos.setOnClickListener(v -> openProductosActivity());
        cardVentasHoy.setOnClickListener(v -> openHistorialVentasActivity());
        cardReporteCaja.setOnClickListener(v -> openReporteCajaActivity());

        btnClientes.setOnClickListener(v -> openClientesActivity());
        btnProveedores.setOnClickListener(v -> openProveedoresActivity());
        btnProductos.setOnClickListener(v -> openProductosActivity());
        btnVentas.setOnClickListener(v -> openVentasActivity());
        btnStats.setOnClickListener(v -> openStatsActivity());
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
                lastVentas = ventas;
                txtVentasHoy.setText(String.valueOf(ventas.size()));
                updateCajaTotal();
            }
        });

        movimientoViewModel.getAllMovimientos().observe(this, movimientos -> {
            if (movimientos != null) {
                lastMovimientos = movimientos;
                updateCajaTotal();
            }
        });
        
        txtDbStatus.setText("Sesion: Administrador");
    }

    private void updateCajaTotal() {
        double totalVentasVal = 0;
        for (Venta v : lastVentas) {
            totalVentasVal += v.getTotal();
        }

        double totalMovimientosVal = 0;
        for (Movimiento m : lastMovimientos) {
            totalMovimientosVal += (m.getIngresos() != null ? m.getIngresos() : 0.0);
            totalMovimientosVal -= (m.getEgresos() != null ? m.getEgresos() : 0.0);
        }

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        txtCajaSaldo.setText(format.format(totalVentasVal + totalMovimientosVal));
    }

    private void openClientesActivity() {
        startActivity(new Intent(this, ClientesActivity.class));
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

    private void openStatsActivity() {
        startActivity(new Intent(this, com.agrovet.pos.activities.StatsActivity.class));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_inicio) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_clientes) {
            openClientesActivity();
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
        } else if (id == R.id.nav_estadisticas) {
            openStatsActivity();
        } else {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private void setupBackPressed() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }
}
