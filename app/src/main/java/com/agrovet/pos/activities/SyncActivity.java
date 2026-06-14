package com.agrovet.pos.activities;

import android.content.Intent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.agrovet.pos.MainActivity;
import com.agrovet.pos.R;
import com.agrovet.pos.utils.ConnectivityTester;
import com.agrovet.pos.utils.DebugLog;
import com.agrovet.pos.viewmodels.ClienteViewModel;
import com.agrovet.pos.viewmodels.ProductoViewModel;
import java.util.Calendar;

public class SyncActivity extends AppCompatActivity {

    private TextView txtDetails;
    private ProgressBar progressBar;
    private Button btnOffline;
    private ClienteViewModel clienteViewModel;
    private ProductoViewModel productoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        txtDetails = findViewById(R.id.txt_sync_details);
        progressBar = findViewById(R.id.progress_sync);
        btnOffline = findViewById(R.id.btn_proceed_offline);

        btnOffline.setOnClickListener(v -> {
            startActivity(new Intent(SyncActivity.this, MainActivity.class));
            finish();
        });

        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
        productoViewModel = new ViewModelProvider(this).get(ProductoViewModel.class);

        startSyncProcess();
    }

    private void startSyncProcess() {
        SharedPreferences prefs = getSharedPreferences("SyncPrefs", Context.MODE_PRIVATE);
        boolean alreadySynced = prefs.getBoolean("is_synced_today", false);

        if (alreadySynced) {
            updateStatus("Información ya cargada. Iniciando...");
            new Handler().postDelayed(() -> {
                startActivity(new Intent(SyncActivity.this, MainActivity.class));
                finish();
            }, 1000);
            return;
        }

        new Thread(() -> {
            try {
                updateStatus("Sincronizando Clientes (Carga Completa)...");
                clienteViewModel.refreshClientes();
                Thread.sleep(3000); 

                updateStatus("Sincronizando Productos (Carga Completa)...");
                productoViewModel.refreshProductos();
                Thread.sleep(3000);

                // Bloqueamos la sincronización hasta que se resetee manualmente
                prefs.edit().putBoolean("is_synced_today", true).apply();

                updateStatus("Entorno listo");
                Thread.sleep(1000);

                runOnUiThread(() -> {
                    startActivity(new Intent(SyncActivity.this, MainActivity.class));
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    updateStatus("Error de conexión con el servidor");
                    txtDetails.setText("No se pudo conectar con " + com.agrovet.pos.utils.Constants.DOMAIN_USUARIOS);
                    progressBar.setVisibility(View.GONE);
                    btnOffline.setVisibility(View.VISIBLE);
                });
            }
        }).start();
    }

    private void updateStatus(String message) {
        runOnUiThread(() -> txtDetails.setText(message));
    }
}
