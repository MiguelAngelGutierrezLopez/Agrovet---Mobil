package com.agrovet.pos.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import java.text.Normalizer;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
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
import com.agrovet.pos.models.Proveedor;
import com.agrovet.pos.viewmodels.ProveedorViewModel;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ProductosActivity extends BaseActivity {

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
    private ProveedorViewModel proveedorViewModel;
    private final List<Producto> productosList = new ArrayList<>();
    private final List<Producto> productosListOriginal = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        viewModel = new ViewModelProvider(this).get(ProductoViewModel.class);
        proveedorViewModel = new ViewModelProvider(this).get(ProveedorViewModel.class);

        initViews();
        setupDrawer();
        setupSpinners();
        setupRecyclerView();
        setupListeners();
        loadProductos();

        // Mensaje de advertencia sobre sincronización de proveedores
        Toast.makeText(this, "Si un proveedor creado en la aplicación movil no está ingresado en el servidor puede generar problemas a la hora de mandar productos nuevos, por favor recuerde mantener estos datos sincronizados", Toast.LENGTH_LONG).show();
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
            getSupportActionBar().setTitle(R.string.titulo_productos);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupSpinners() {
        List<String> categorias = new ArrayList<>();
        categorias.add("Todas las categorías");
        categorias.add("ANTIBIOTICOS");
        categorias.add("BIOESTIMULANTES");
        categorias.add("BIOLOGICOS");
        categorias.add("COADYUVANTES");
        categorias.add("CONCENTRADOS");
        categorias.add("CONCETRADO AVES PRODUCCION");
        categorias.add("CONCENTRADOS GATOS");
        categorias.add("CONCENTRADOS PERROS");
        categorias.add("ENMIENDA");
        categorias.add("FERTILIZANTES");
        categorias.add("FUNGICIDAS");
        categorias.add("HERBICIDAS");
        categorias.add("INSECTICIDAS");
        categorias.add("MAIZ");
        categorias.add("MASCOTAS");
        categorias.add("REGULADOR DE CRECIMIENTO");
        categorias.add("REPUESTOS, BOMBAS Y ESTACIONARIAS");
        categorias.add("SALES GANADERAS");
        categorias.add("SEMILLAS");
        categorias.add("VETERINARIA");


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
            filtrados.removeIf(p -> !p.getNombre().toLowerCase().contains(query));
        }

        if (!categoriaSeleccionada.equals("Todas las categorías")) {
            filtrados.removeIf(p -> !p.getCategoria().equals(categoriaSeleccionada));
        }

        if (stockFiltro.equals("Stock bajo (≤5)")) {
            filtrados.removeIf(p -> p.getCantidad() > 5);
        } else if (stockFiltro.equals("Stock medio (6-15)")) {
            filtrados.removeIf(p -> p.getCantidad() < 6 || p.getCantidad() > 15);
        } else if (stockFiltro.equals("Stock alto (>15)")) {
            filtrados.removeIf(p -> p.getCantidad() <= 15);
        }

        productosList.clear();
        productosList.addAll(filtrados);
        adapter.notifyDataSetChanged();
    }

    private String[] CATEGORIAS = {
            "ANTIBIOTICOS", "BIOESTIMULANTES", "BIOLOGICOS", "COADYUVANTES", "CONCENTRADOS",
            "CONCENTRADO AVES PRODUCCION", "CONCENTRADOS GATOS", "CONCENTRADOS PERROS", "ENMIENDA",
            "FERTILIZANTES", "FUNGICIDAS", "HERBICIDAS", "INSECTICIDAS", "MAIZ", "MASCOTAS",
            "REGULADOR DE CRECIMIENTO", "REPUESTOS, BOMBAS Y ESTACIONARIAS", "SALES GANADERAS",
            "SEMILLAS", "VETERINARIA"
    };

    private void mostrarDialogoProducto(Producto producto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_producto, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        TextView tituloDialog = view.findViewById(R.id.titulo_dialog);
        EditText etCodigo = view.findViewById(R.id.et_codigo);
        TextInputEditText etNombre = view.findViewById(R.id.et_nombre);
        TextInputEditText etPrecio = view.findViewById(R.id.et_precio);
        TextInputEditText etStock = view.findViewById(R.id.et_stock);
        ChipGroup cgCategorias = view.findViewById(R.id.cg_categorias);
        TextInputEditText etPresentacion = view.findViewById(R.id.et_presentacion);
        AutoCompleteTextView etProveedor = view.findViewById(R.id.et_producto_proveedor);
        Button btnCancelar = view.findViewById(R.id.btn_cancelar);
        Button btnGuardar = view.findViewById(R.id.btn_guardar);

        // Llenar Proveedores
        final Map<String, String> mapProveedores = new HashMap<>();
        proveedorViewModel.getProveedores().observe(this, listaProv -> {
            if (listaProv != null) {
                List<String> nombres = new ArrayList<>();
                for (Proveedor prov : listaProv) {
                    String label = prov.getNombreEmpresa() + " (" + prov.getNombreProveedor() + ")";
                    nombres.add(label);
                    mapProveedores.put(label, prov.getTelefono());
                }
                
                if (nombres.isEmpty()) {
                    nombres.add("No hay registros");
                }

                ArrayAdapter<String> provAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nombres);
                etProveedor.setAdapter(provAdapter);
                
                // Si estamos editando y el producto tiene proveedor, intentar seleccionarlo por nombre
                if (producto != null && producto.getProveedor() != null) {
                    for (Proveedor prov : listaProv) {
                        if (prov.getTelefono().equals(producto.getProveedor())) {
                            String label = prov.getNombreEmpresa() + " (" + prov.getNombreProveedor() + ")";
                            etProveedor.setText(label, false);
                            break;
                        }
                    }
                }
            }
        });

        etProveedor.setOnClickListener(v -> etProveedor.showDropDown());

        // Llenar Chips de Categorias
        for (String cat : CATEGORIAS) {
            Chip chip = new Chip(this);
            chip.setText(cat);
            chip.setCheckable(true);
            cgCategorias.addView(chip);
            if (producto != null && cat.equalsIgnoreCase(producto.getCategoria())) {
                chip.setChecked(true);
            }
        }

        boolean isEditando = producto != null;

        if (isEditando) {
            tituloDialog.setText("Editar Producto");
            if (etCodigo != null) etCodigo.setText(String.valueOf(producto.getId()));
            etNombre.setText(producto.getNombre());
            etPrecio.setText(String.valueOf(producto.getPrecioVenta() != null ? producto.getPrecioVenta() : 0));
            etStock.setText(String.valueOf(producto.getCantidad() != null ? producto.getCantidad() : 0));
            etPresentacion.setText(producto.getPresentacion());
        } else {
            tituloDialog.setText("Nuevo Producto");
        }

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String precioStr = etPrecio.getText().toString().trim();
            String stockStr = etStock.getText().toString().trim();
            String presentacion = etPresentacion.getText().toString().trim();
            String nombreProv = etProveedor.getText().toString().trim();
            String telefonoProv = mapProveedores.get(nombreProv);
            
            int selectedChipId = cgCategorias.getCheckedChipId();

            if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty() || selectedChipId == -1) {
                Toast.makeText(this, "Nombre, precio, unidades y categoría son requeridos", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int precio = Integer.parseInt(precioStr);
                int unidades = Integer.parseInt(stockStr);
                String categoria = ((Chip) view.findViewById(selectedChipId)).getText().toString().toUpperCase();
                categoria = Normalizer.normalize(categoria, Normalizer.Form.NFD).replaceAll("\\p{M}", "");

                if (isEditando) {
                    producto.setNombre(nombre);
                    producto.setPrecioVenta(precio);
                    producto.setCantidad(unidades);
                    producto.setCategoria(categoria);
                    producto.setPresentacion(presentacion);
                    producto.setProveedor(telefonoProv != null ? telefonoProv : "");
                    viewModel.updateProducto(producto);
                    Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show();
                } else {
                    Producto nuevoProducto = new Producto(0, nombre, "", categoria, unidades, presentacion, telefonoProv != null ? telefonoProv : "", 0, precio);
                    nuevoProducto.setSynced(false);
                    viewModel.createProducto(nuevoProducto);
                    Toast.makeText(this, "Producto creado exitosamente", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Precio y unidades deben ser numéricos", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void onEditarClick(Producto producto) {
        mostrarDialogoProducto(producto);
    }

    private void onEliminarClick(Producto producto) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.btn_eliminar)
                .setMessage("¿Desea eliminar " + producto.getNombre() + "?")
                .setPositiveButton("Si", (dialog, which) -> {
                    viewModel.deleteProducto(producto.getId());
                    Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
