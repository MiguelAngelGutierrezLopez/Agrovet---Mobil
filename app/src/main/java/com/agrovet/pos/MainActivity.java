package com.agrovet.pos;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import com.agrovet.pos.activities.ClientesActivity;
import com.agrovet.pos.activities.ProductosActivity;
import com.agrovet.pos.activities.ProveedoresActivity;
import com.agrovet.pos.viewmodels.ClienteViewModel;
import com.agrovet.pos.viewmodels.ProductoViewModel;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView txtTotalClientes, txtTotalProductos, txtDbStatus;
    private CardView cardClientes, cardProductos;
    private CardView btnClientes, btnProveedores, btnProductos, btnVentas;
    
    private ClienteViewModel clienteViewModel;
    private ProductoViewModel productoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
        productoViewModel = new ViewModelProvider(this).get(ProductoViewModel.class);

        initViews();
        setupToolbar();
        setupNavigationDrawer();
        setupClickListeners();
        loadDashboardData();
        setupBackPressed();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        txtTotalClientes = findViewById(R.id.txt_total_clientes);
        txtTotalProductos = findViewById(R.id.txt_total_productos);
        txtDbStatus = findViewById(R.id.txt_db_status);
        cardClientes = findViewById(R.id.card_clientes);
        cardProductos = findViewById(R.id.card_productos);
        btnClientes = findViewById(R.id.btn_clientes);
        btnProveedores = findViewById(R.id.btn_proveedores);
        btnProductos = findViewById(R.id.btn_productos);
        btnVentas = findViewById(R.id.btn_ventas);
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
        btnClientes.setOnClickListener(v -> openClientesActivity());
        btnProveedores.setOnClickListener(v -> openProveedoresActivity());
        btnProductos.setOnClickListener(v -> openProductosActivity());
        btnVentas.setOnClickListener(v ->
                Toast.makeText(this, "Módulo de Ventas - Próximamente", Toast.LENGTH_SHORT).show());
    }

    private void loadDashboardData() {
        clienteViewModel.getClientes().observe(this, clientes -> {
            if (clientes != null) {
                txtTotalClientes.setText(String.valueOf(clientes.size()));
            }
        });
        
        productoViewModel.getProductos().observe(this, productos -> {
            if (productos != null) {
                txtTotalProductos.setText(String.valueOf(productos.size()));
            }
        });
        
        txtDbStatus.setText("Local (SQLite/Room)");
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
