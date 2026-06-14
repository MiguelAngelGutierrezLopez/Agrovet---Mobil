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
import com.agrovet.pos.adapters.ProductoAdapter;
import com.agrovet.pos.models.Producto;
import com.agrovet.pos.viewmodels.ProductoViewModel;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class ProductosActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rvProductos;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private EditText etBuscar;
    private View btnAgregar;
    private Button btnBuscar;
    private Spinner spinnerCategoria, spinnerStock;

    private ProductoAdapter adapter;
    private ProductoViewModel viewModel;
    private List<Producto> productosList = new ArrayList<>();
    private List<Producto> productosListOriginal = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        viewModel = new ViewModelProvider(this).get(ProductoViewModel.class);

        initViews();
        setupToolbar();
        setupSpinners();
        setupRecyclerView();
        setupListeners();
        loadProductos();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvProductos = findViewById(R.id.rv_productos);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        progressBar = findViewById(R.id.progressBar);
        etBuscar = findViewById(R.id.et_buscar);
        btnBuscar = findViewById(R.id.btn_buscar);
        btnAgregar = findViewById(R.id.btn_agregar);
        spinnerCategoria = findViewById(R.id.spinner_categoria);
        spinnerStock = findViewById(R.id.spinner_stock);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Productos");
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupSpinners() {
        List<String> categorias = new ArrayList<>();
        categorias.add("Todas las categorías");
        categorias.add("Vacunas");
        categorias.add("Medicamentos");
        categorias.add("Fertilizantes");
        categorias.add("Semillas");
        categorias.add("Equipos");

        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(categoriaAdapter);

        List<String> stockFiltros = new ArrayList<>();
        stockFiltros.add("Todos los productos");
        stockFiltros.add("Stock bajo (≤5)");
        stockFiltros.add("Stock medio (6-15)");
        stockFiltros.add("Stock alto (>15)");

        ArrayAdapter<String> stockAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stockFiltros);
        stockAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStock.setAdapter(stockAdapter);

        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarFiltrosLocales();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerStock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarFiltrosLocales();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupRecyclerView() {
        adapter = new ProductoAdapter(productosList, new ProductoAdapter.OnProductoActionListener() {
            @Override
            public void onEditar(Producto producto) {
                onEditarClick(producto);
            }

            @Override
            public void onEliminar(Producto producto) {
                onEliminarClick(producto);
            }
        });
        rvProductos.setLayoutManager(new LinearLayoutManager(this));
        rvProductos.setAdapter(adapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(() -> {
            loadProductos();
            swipeRefresh.setRefreshing(false);
        });

        btnBuscar.setOnClickListener(v -> aplicarFiltrosLocales());
        btnAgregar.setOnClickListener(v -> mostrarDialogoProducto(null));
    }

    private void loadProductos() {
        progressBar.setVisibility(View.VISIBLE);
        viewModel.getProductos().observe(this, productos -> {
            progressBar.setVisibility(View.GONE);
            if (productos != null) {
                productosListOriginal.clear();
                productosListOriginal.addAll(productos);
                aplicarFiltrosLocales();
            }
        });
    }

    private void aplicarFiltrosLocales() {
        String query = etBuscar.getText().toString().toLowerCase().trim();
        String categoriaSeleccionada = spinnerCategoria.getSelectedItem().toString();
        String stockFiltro = spinnerStock.getSelectedItem().toString();

        List<Producto> filtrados = new ArrayList<>(productosListOriginal);

        if (!query.isEmpty()) {
            filtrados.removeIf(p -> !p.getNombre().toLowerCase().contains(query) &&
                    !String.valueOf(p.getCodigo()).contains(query));
        }

        if (!categoriaSeleccionada.equals("Todas las categorías")) {
            filtrados.removeIf(p -> !p.getCategoria().equals(categoriaSeleccionada));
        }

        if (stockFiltro.equals("Stock bajo (≤5)")) {
            filtrados.removeIf(p -> p.getUnidades() > 5);
        } else if (stockFiltro.equals("Stock medio (6-15)")) {
            filtrados.removeIf(p -> p.getUnidades() < 6 || p.getUnidades() > 15);
        } else if (stockFiltro.equals("Stock alto (>15)")) {
            filtrados.removeIf(p -> p.getUnidades() <= 15);
        }

        productosList.clear();
        productosList.addAll(filtrados);
        adapter.notifyDataSetChanged();
    }

    private void mostrarDialogoProducto(Producto producto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_producto, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        TextView tituloDialog = view.findViewById(R.id.titulo_dialog);
        TextInputEditText etCodigo = view.findViewById(R.id.et_codigo);
        TextInputEditText etNombre = view.findViewById(R.id.et_nombre);
        TextInputEditText etPrecio = view.findViewById(R.id.et_precio);
        TextInputEditText etStock = view.findViewById(R.id.et_stock);
        TextInputEditText etCategoria = view.findViewById(R.id.et_categoria);
        TextInputEditText etPresentacion = view.findViewById(R.id.et_proveedor);
        Button btnCancelar = view.findViewById(R.id.btn_cancelar);
        Button btnGuardar = view.findViewById(R.id.btn_guardar);

        boolean isEditando = producto != null;

        if (isEditando) {
            tituloDialog.setText("Editar Producto");
            etCodigo.setText(String.valueOf(producto.getCodigo()));
            etNombre.setText(producto.getNombre());
            etPrecio.setText(String.valueOf(producto.getPrecioVenta()));
            etStock.setText(String.valueOf(producto.getUnidades()));
            etCategoria.setText(producto.getCategoria());
            etPresentacion.setText(producto.getPresentacion());
            etCodigo.setEnabled(false);
        } else {
            tituloDialog.setText("Nuevo Producto");
            etCodigo.setEnabled(true);
        }

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            String codigoStr = etCodigo.getText().toString().trim();
            String nombre = etNombre.getText().toString().trim();
            String precioStr = etPrecio.getText().toString().trim();
            String stockStr = etStock.getText().toString().trim();
            String categoria = etCategoria.getText().toString().trim();
            String presentacion = etPresentacion.getText().toString().trim();

            if (codigoStr.isEmpty() || nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
                Toast.makeText(this, "Código, nombre, precio y unidades son requeridos", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                long codigo = Long.parseLong(codigoStr);
                int precio = Integer.parseInt(precioStr);
                int unidades = Integer.parseInt(stockStr);

                if (isEditando) {
                    producto.setNombre(nombre);
                    producto.setPrecioVenta(precio);
                    producto.setUnidades(unidades);
                    producto.setCategoria(categoria);
                    producto.setPresentacion(presentacion);
                    viewModel.updateProducto(producto);
                    Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show();
                } else {
                    Producto nuevoProducto = new Producto(nombre, codigo, categoria, precio, unidades, presentacion);
                    viewModel.createProducto(nuevoProducto);
                    Toast.makeText(this, "Producto creado exitosamente", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Código, precio y unidades deben ser numéricos", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void onEditarClick(Producto producto) {
        mostrarDialogoProducto(producto);
    }

    private void onEliminarClick(Producto producto) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Producto")
                .setMessage("¿Desea eliminar " + producto.getNombre() + "?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    viewModel.deleteProducto(producto.getCodigo());
                    Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
