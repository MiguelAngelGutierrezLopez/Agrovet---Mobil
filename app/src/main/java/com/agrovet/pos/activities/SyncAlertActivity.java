package com.agrovet.pos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.agrovet.pos.MainActivity;
import com.agrovet.pos.R;
import com.agrovet.pos.utils.SyncManager;

public class SyncAlertActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_alert);

        String modulo = getIntent().getStringExtra("modulo");
        TextView txtMessage = findViewById(R.id.txt_alert_message);
        if (modulo != null) {
            txtMessage.setText("Hemos detectado que hubo un cambio en " + modulo + ". ¿Quiere reiniciar la aplicación ahora?");
        }

        Button btnNow = findViewById(R.id.btn_sync_now);
        Button btnLater = findViewById(R.id.btn_sync_later);

        btnNow.setOnClickListener(v -> {
            Toast.makeText(this, "Reiniciando y sincronizando...", Toast.LENGTH_SHORT).show();
            
            // Simular reinicio volviendo a MainActivity con un flag de sync
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("perform_sync_pull", true);
            startActivity(intent);
            finish();
        });

        btnLater.setOnClickListener(v -> finish());
    }
}
