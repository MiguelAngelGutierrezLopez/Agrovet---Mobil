package com.agrovet.pos.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.agrovet.pos.R;
import com.agrovet.pos.adapters.ProveedorAdapter;
import com.agrovet.pos.models.Proveedor;
import com.agrovet.pos.viewmodels.ProveedorViewModel;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProveedoresActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rvProveedores;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private EditText etBuscar;
    private View btnAgregar;
    private Button btnBuscar;

    private ProveedorAdapter adapter;
    private ProveedorViewModel viewModel;
    private final List<Proveedor> proveedoresList = new ArrayList<>();
    private final List<Proveedor> proveedoresListOriginal = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proveedores);

        viewModel = new ViewModelProvider(this).get(ProveedorViewModel.class);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();
        loadProveedores();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvProveedores = findViewById(R.id.rv_proveedores);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        progressBar = findViewById(R.id.progressBar);
        etBuscar = findViewById(R.id.et_buscar);
        btnBuscar = findViewById(R.id.btn_buscar);
        btnAgregar = findViewById(R.id.btn_agregar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.titulo_proveedores);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new ProveedorAdapter(proveedoresList, new ProveedorAdapter.OnProveedorActionListener() {
            @Override
            public void onEditar(Proveedor proveedor) {
                onEditarClick(proveedor);
            }

            @Override
            public void onEliminar(Proveedor proveedor) {
                onEliminarClick(proveedor);
            }
        });
        rvProveedores.setLayoutManager(new LinearLayoutManager(this));
        rvProveedores.setAdapter(adapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(() -> {
            loadProveedores();
            swipeRefresh.setRefreshing(false);
        });

        btnBuscar.setOnClickListener(v -> buscarProveedores());
        btnAgregar.setOnClickListener(v -> mostrarDialogoProveedor(null));
    }

    private void loadProveedores() {
        progressBar.setVisibility(View.VISIBLE);
        viewModel.getProveedores().observe(this, proveedores -> {
            progressBar.setVisibility(View.GONE);
            if (proveedores != null) {
                proveedoresListOriginal.clear();
                proveedoresListOriginal.addAll(proveedores);
                aplicarFiltroLocal();
            }
        });
    }

    private void buscarProveedores() {
        aplicarFiltroLocal();
        if (proveedoresList.isEmpty()) {
            Toast.makeText(this, "No se encontraron proveedores", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void aplicarFiltroLocal() {
        String query = etBuscar.getText().toString().toLowerCase().trim();
        proveedoresList.clear();
        if (query.isEmpty()) {
            proveedoresList.addAll(proveedoresListOriginal);
        } else {
            for (Proveedor proveedor : proveedoresListOriginal) {
                if (proveedor.getNombreEmpresa().toLowerCase().contains(query) ||
                        proveedor.getNombreProveedor().toLowerCase().contains(query)) {
                    proveedoresList.add(proveedor);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void mostrarDialogoProveedor(Proveedor proveedor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_proveedor, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        TextView tituloDialog = view.findViewById(R.id.titulo_dialog);
        TextInputEditText etTelefono = view.findViewById(R.id.et_telefono);
        TextInputEditText etNombreEmpresa = view.findViewById(R.id.et_nombre_empresa);
        TextInputEditText etNombreProveedor = view.findViewById(R.id.et_nombre_proveedor);
        TextInputEditText etProductos = view.findViewById(R.id.et_productos);
        Button btnCancelar = view.findViewById(R.id.btn_cancelar);
        Button btnGuardar = view.findViewById(R.id.btn_guardar);

        boolean isEditando = proveedor != null;

        if (isEditando) {
            tituloDialog.setText("Editar Proveedor");
            etTelefono.setText(proveedor.getTelefono());
            etNombreEmpresa.setText(proveedor.getNombreEmpresa());
            etNombreProveedor.setText(proveedor.getNombreProveedor());
            etProductos.setText(proveedor.getProductos());
            etTelefono.setEnabled(false);
        } else {
            tituloDialog.setText("Nuevo Proveedor");
            etTelefono.setEnabled(true);
        }

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            String telefono = etTelefono.getText().toString().trim();
            String nombreEmpresa = etNombreEmpresa.getText().toString().trim();
            String nombreProveedor = etNombreProveedor.getText().toString().trim();
            String productos = etProductos.getText().toString().trim();

            if (telefono.isEmpty() || nombreEmpresa.isEmpty() || nombreProveedor.isEmpty()) {
                Toast.makeText(this, "Telefono, empresa y nombre son requeridos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEditando) {
                proveedor.setNombreEmpresa(nombreEmpresa);
                proveedor.setNombreProveedor(nombreProveedor);
                proveedor.setProducto(productos);
                viewModel.updateProveedor(proveedor);
                Toast.makeText(this, "Proveedor actualizado", Toast.LENGTH_SHORT).show();
            } else {
                String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                Proveedor nuevoProveedor = new Proveedor(telefono, nombreEmpresa, nombreProveedor, "", "activo", fecha, productos);
                viewModel.createProveedor(nuevoProveedor);
                Toast.makeText(this, "Proveedor creado exitosamente", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void onEditarClick(Proveedor proveedor) {
        mostrarDialogoProveedor(proveedor);
    }

    private void onEliminarClick(Proveedor proveedor) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.btn_eliminar)
                .setMessage("¿Desea eliminar a " + proveedor.getNombreEmpresa() + "?")
                .setPositiveButton("Si", (dialog, which) -> {
                    viewModel.deleteProveedor(proveedor.getTelefono());
                    Toast.makeText(this, "Proveedor eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
