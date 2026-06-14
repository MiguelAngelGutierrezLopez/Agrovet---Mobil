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
import com.agrovet.pos.adapters.ClienteAdapter;
import com.agrovet.pos.models.Cliente;
import com.agrovet.pos.viewmodels.ClienteViewModel;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClientesActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView rvClientes;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private EditText etBuscar;
    private View btnAgregar;
    private Button btnBuscar;

    private ClienteAdapter adapter;
    private ClienteViewModel viewModel;
    private final List<Cliente> clientesList = new ArrayList<>();
    private final List<Cliente> clientesListOriginal = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes);

        viewModel = new ViewModelProvider(this).get(ClienteViewModel.class);

        initViews();
        setupDrawer();
        setupRecyclerView();
        setupListeners();
        loadClientes();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvClientes = findViewById(R.id.rv_clientes);
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
            getSupportActionBar().setTitle(R.string.titulo_clientes);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new ClienteAdapter(clientesList, new ClienteAdapter.OnClienteActionListener() {
            @Override
            public void onEditar(Cliente cliente) {
                onEditarClick(cliente);
            }

            @Override
            public void onEliminar(Cliente cliente) {
                onEliminarClick(cliente);
            }
        });
        rvClientes.setLayoutManager(new LinearLayoutManager(this));
        rvClientes.setAdapter(adapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(() -> {
            loadClientes();
            swipeRefresh.setRefreshing(false);
        });

        btnBuscar.setOnClickListener(v -> buscarClientes());
        btnAgregar.setOnClickListener(v -> mostrarDialogoCliente(null));
    }

    private void loadClientes() {
        progressBar.setVisibility(View.VISIBLE);
        viewModel.getClientes().observe(this, clientes -> {
            progressBar.setVisibility(View.GONE);
            if (clientes != null) {
                clientesListOriginal.clear();
                clientesListOriginal.addAll(clientes);
                aplicarFiltroLocal();
            }
        });
    }

    private void buscarClientes() {
        aplicarFiltroLocal();
        if (clientesList.isEmpty()) {
            Toast.makeText(this, "No se encontraron clientes", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void aplicarFiltroLocal() {
        String query = etBuscar.getText().toString().toLowerCase().trim();
        clientesList.clear();
        if (query.isEmpty()) {
            clientesList.addAll(clientesListOriginal);
        } else {
            for (Cliente cliente : clientesListOriginal) {
                if (cliente.getNombre().toLowerCase().contains(query) ||
                        cliente.getCedula().contains(query)) {
                    clientesList.add(cliente);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void mostrarDialogoCliente(Cliente cliente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cliente, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        TextView tituloDialog = view.findViewById(R.id.titulo_dialog);
        TextInputEditText etCedula = view.findViewById(R.id.et_cedula);
        TextInputEditText etNombre = view.findViewById(R.id.et_nombre);
        TextInputEditText etTelefono = view.findViewById(R.id.et_telefono);
        TextInputEditText etCorreo = view.findViewById(R.id.et_correo);
        TextInputEditText etDireccion = view.findViewById(R.id.et_direccion);
        Button btnCancelar = view.findViewById(R.id.btn_cancelar);
        Button btnGuardar = view.findViewById(R.id.btn_guardar);

        boolean isEditando = cliente != null;

        if (isEditando) {
            tituloDialog.setText("Editar Cliente");
            etCedula.setText(cliente.getCedula());
            etNombre.setText(cliente.getNombre());
            etTelefono.setText(cliente.getTelefono());
            etCorreo.setText(cliente.getCorreo());
            etDireccion.setText(cliente.getDireccion());
            etCedula.setEnabled(false);
        } else {
            tituloDialog.setText("Nuevo Cliente");
            etCedula.setEnabled(true);
        }

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            String cedula = etCedula.getText().toString().trim();
            String nombre = etNombre.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String correo = etCorreo.getText().toString().trim();
            String direccion = etDireccion.getText().toString().trim();

            if (cedula.isEmpty() || nombre.isEmpty()) {
                Toast.makeText(this, "Cedula y nombre son requeridos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEditando) {
                cliente.setNombre(nombre);
                cliente.setTelefono(telefono);
                cliente.setCorreo(correo);
                cliente.setDireccion(direccion);
                viewModel.updateCliente(cliente);
                Toast.makeText(this, "Cliente actualizado", Toast.LENGTH_SHORT).show();
            } else {
                String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                Cliente nuevoCliente = new Cliente(cedula, nombre, telefono, correo, direccion, fecha);
                viewModel.createCliente(nuevoCliente);
                Toast.makeText(this, "Cliente creado exitosamente", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void onEditarClick(Cliente cliente) {
        mostrarDialogoCliente(cliente);
    }

    private void onEliminarClick(Cliente cliente) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.btn_eliminar)
                .setMessage("¿Desea eliminar a " + cliente.getNombre() + "?")
                .setPositiveButton("Si", (dialog, which) -> {
                    viewModel.deleteCliente(cliente.getCedula());
                    Toast.makeText(this, "Cliente eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
