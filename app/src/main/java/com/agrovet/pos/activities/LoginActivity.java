package com.agrovet.pos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.agrovet.pos.R;
import com.agrovet.pos.utils.AppLogger;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            try {
                String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
                String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

                if ("admin".equals(username) && "AgroVet".equals(password)) {
                    AppLogger.i("Login exitoso para el usuario administrador");
                    startActivity(new Intent(this, SyncActivity.class));
                    finish();
                } else {
                    AppLogger.w("Intento de login fallido: " + username);
                    Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                AppLogger.e("Error durante el proceso de login", e);
                Toast.makeText(this, "Error en el sistema", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
