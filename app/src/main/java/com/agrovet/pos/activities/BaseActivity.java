package com.agrovet.pos.activities;

import android.content.Intent;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.agrovet.pos.MainActivity;
import com.agrovet.pos.R;
import com.google.android.material.navigation.NavigationView;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected Toolbar toolbar;

    protected void setupDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        if (drawerLayout != null && navigationView != null && toolbar != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;

        if (id == R.id.nav_inicio) {
            if (!(this instanceof MainActivity)) {
                intent = new Intent(this, MainActivity.class);
            }
        } else if (id == R.id.nav_clientes) {
            if (!(this instanceof ClientesActivity)) {
                intent = new Intent(this, ClientesActivity.class);
            }
        } else if (id == R.id.nav_productos) {
            if (!(this instanceof ProductosActivity)) {
                intent = new Intent(this, ProductosActivity.class);
            }
        } else if (id == R.id.nav_proveedores) {
            if (!(this instanceof ProveedoresActivity)) {
                intent = new Intent(this, ProveedoresActivity.class);
            }
        } else if (id == R.id.nav_ventas) {
            if (!(this instanceof VentasActivity)) {
                intent = new Intent(this, VentasActivity.class);
            }
        } else if (id == R.id.nav_historial) {
            if (!(this instanceof HistorialVentasActivity)) {
                intent = new Intent(this, HistorialVentasActivity.class);
            }
        } else if (id == R.id.nav_reporte_caja) {
            if (!(this instanceof ReporteCajaActivity)) {
                intent = new Intent(this, ReporteCajaActivity.class);
            }
        }

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }

        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
