package com.agrovet.pos.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.agrovet.pos.R;
import com.agrovet.pos.utils.AppLogger;
import java.util.List;

public class LogViewerActivity extends AppCompatActivity {

    private TextView txtLogs;
    private Button btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_viewer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        txtLogs = findViewById(R.id.txt_logs);
        btnClear = findViewById(R.id.btn_clear_logs);

        loadLogs();

        btnClear.setOnClickListener(v -> {
            AppLogger.clearHistory();
            loadLogs();
        });
    }

    private void loadLogs() {
        List<String> logs = AppLogger.getLogHistory();
        StringBuilder sb = new StringBuilder();
        for (String log : logs) {
            sb.append(log).append("\n\n");
        }
        if (logs.isEmpty()) {
            sb.append("No hay registros disponibles.");
        }
        txtLogs.setText(sb.toString());
    }
}
