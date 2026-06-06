package com.agrovet.pos.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.agrovet.pos.R;
import com.agrovet.pos.adapters.CartAdapter;
import com.agrovet.pos.adapters.ProductoAdapter;
import com.agrovet.pos.models.Abono;
import com.agrovet.pos.models.CartItem;
import com.agrovet.pos.models.Cliente;
import com.agrovet.pos.models.Producto;
import com.agrovet.pos.models.Venta;
import com.agrovet.pos.utils.AppLogger;
import com.agrovet.pos.viewmodels.ClienteViewModel;
import com.agrovet.pos.viewmodels.ProductoViewModel;
import com.agrovet.pos.viewmodels.VentaViewModel;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VentasActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rvCatalog, rvCart;
    private EditText etBuscar, etDescuento, etMontoRecibido, etAnticipo;
    private AutoCompleteTextView etBuscarCliente;
    private TextView txtTotal, txtResumenSubtotal, txtStep1Indicator, txtStep2Indicator, txtVuelto;
    private Button btnFinalizar, btnNextStep, btnBackStep, btnVentaEspecifica;
    private RadioGroup rgMetodoPago;
    private View layoutStep1, layoutStep2, layoutAnticipo;
    
    private ProductoViewModel productoViewModel;
    private VentaViewModel ventaViewModel;
    private ClienteViewModel clienteViewModel;
    
    private ProductoAdapter catalogAdapter;
    private CartAdapter cartAdapter;
    
    private final List<Producto> catalogList = new ArrayList<>();
    private final List<Producto> catalogListFull = new ArrayList<>();
    private final List<CartItem> cartList = new ArrayList<>();
    private final List<Cliente> clientesList = new ArrayList<>();
    
    private double subtotalVenta = 0;
    private double totalFinal = 0;
    private Cliente selectedCliente = null;
    private boolean isVentaEspecifica = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas);

        productoViewModel = new ViewModelProvider(this).get(ProductoViewModel.class);
        ventaViewModel = new ViewModelProvider(this).get(VentaViewModel.class);
        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);

        initViews();
        setupToolbar();
        setupRecyclerViews();
        setupListeners();
        loadCatalog();
        loadClientes();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvCatalog = findViewById(R.id.rv_productos_disponibles);
        rvCart = findViewById(R.id.rv_carrito);
        etBuscar = findViewById(R.id.et_buscar_producto);
        etBuscarCliente = findViewById(R.id.et_buscar_cliente);
        etDescuento = findViewById(R.id.et_descuento);
        etMontoRecibido = findViewById(R.id.et_monto_recibido);
        etAnticipo = findViewById(R.id.et_anticipo);
        
        txtTotal = findViewById(R.id.txt_total_venta);
        txtResumenSubtotal = findViewById(R.id.txt_resumen_subtotal);
        txtStep1Indicator = findViewById(R.id.step1_indicator);
        txtStep2Indicator = findViewById(R.id.step2_indicator);
        txtVuelto = findViewById(R.id.txt_vuelto);
        
        btnNextStep = findViewById(R.id.btn_next_step);
        btnBackStep = findViewById(R.id.btn_back_to_step1);
        btnFinalizar = findViewById(R.id.btn_finalizar_venta);
        btnVentaEspecifica = findViewById(R.id.btn_venta_especifica);
        
        rgMetodoPago = findViewById(R.id.rg_metodo_pago);
        
        layoutStep1 = findViewById(R.id.layout_step1);
        layoutStep2 = findViewById(R.id.layout_step2);
        layoutAnticipo = findViewById(R.id.layout_anticipo);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerViews() {
        catalogAdapter = new ProductoAdapter(catalogList, new ProductoAdapter.OnProductoActionListener() {
            @Override
            public void onEditar(Producto producto) { addToCart(producto); }
            @Override
            public void onEliminar(Producto producto) { addToCart(producto); }
            @Override
            public void onAddCart(Producto producto) { addToCart(producto); }
        });
        rvCatalog.setLayoutManager(new GridLayoutManager(this, 2));
        rvCatalog.setAdapter(catalogAdapter);

        cartAdapter = new CartAdapter(cartList, item -> {
            cartList.remove(item);
            updateTotals();
            cartAdapter.notifyDataSetChanged();
        });
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setAdapter(cartAdapter);
    }

    private void setupListeners() {
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filterCatalog(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnVentaEspecifica.setOnClickListener(v -> {
            isVentaEspecifica = !isVentaEspecifica;
            etBuscarCliente.setVisibility(isVentaEspecifica ? View.VISIBLE : View.GONE);
            btnVentaEspecifica.setText(isVentaEspecifica ? "VENTA GENERAL" : "VENTA ESPECÍFICA");
            if (!isVentaEspecifica) {
                selectedCliente = null;
                etBuscarCliente.setText("");
            }
        });

        etBuscarCliente.setOnItemClickListener((parent, view, position, id) -> {
            selectedCliente = (Cliente) parent.getItemAtPosition(position);
            etBuscarCliente.setText(selectedCliente.getNombre());
            Toast.makeText(this, "Cliente seleccionado: " + selectedCliente.getNombre(), Toast.LENGTH_SHORT).show();
        });

        btnNextStep.setOnClickListener(v -> {
            if (cartList.isEmpty()) {
                Toast.makeText(this, "Añada productos primero", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isVentaEspecifica && selectedCliente == null) {
                Toast.makeText(this, "Seleccione un cliente válido", Toast.LENGTH_SHORT).show();
                return;
            }
            showStep(2);
        });

        btnBackStep.setOnClickListener(v -> showStep(1));

        etDescuento.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updateTotals(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        etMontoRecibido.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updateTotals(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        rgMetodoPago.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_credito) {
                if (!isVentaEspecifica || selectedCliente == null) {
                    Toast.makeText(this, "Crédito solo disponible para clientes específicos", Toast.LENGTH_LONG).show();
                    rgMetodoPago.check(R.id.rb_contado);
                } else {
                    layoutAnticipo.setVisibility(View.VISIBLE);
                }
            } else {
                layoutAnticipo.setVisibility(View.GONE);
            }
        });

        btnFinalizar.setOnClickListener(v -> {
            try {
                guardarVenta();
            } catch (Exception e) {
                AppLogger.e("Error al finalizar venta", e);
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showStep(int step) {
        if (step == 1) {
            layoutStep1.setVisibility(View.VISIBLE);
            layoutStep2.setVisibility(View.GONE);
            txtStep1Indicator.setTextColor(getColor(R.color.teal));
            txtStep2Indicator.setTextColor(getColor(R.color.gris_medio));
        } else {
            layoutStep1.setVisibility(View.GONE);
            layoutStep2.setVisibility(View.VISIBLE);
            txtStep1Indicator.setTextColor(getColor(R.color.gris_medio));
            txtStep2Indicator.setTextColor(getColor(R.color.teal));
        }
    }

    private void guardarVenta() {
        String fechaDia = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String fechaHora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        
        int selectedId = rgMetodoPago.getCheckedRadioButtonId();
        String metodo = "Contado";
        if (selectedId == R.id.rb_credito) metodo = "Crédito";
        else if (selectedId == R.id.rb_banco) metodo = "Banco";

        double descuento = 0;
        try { descuento = Double.parseDouble(etDescuento.getText().toString()); } catch (Exception ignored) {}

        Venta nuevaVenta = new Venta();
        nuevaVenta.setFechaDia(fechaDia);
        nuevaVenta.setFechaHora(fechaHora);
        nuevaVenta.setNombreCliente(selectedCliente != null ? selectedCliente.getNombre() : "Cliente Final");
        nuevaVenta.setClienteCedula(selectedCliente != null ? selectedCliente.getCedula() : null);
        nuevaVenta.setTipoPago(metodo);
        nuevaVenta.setSubtotal(subtotalVenta);
        nuevaVenta.setDescuento(descuento);
        nuevaVenta.setTotal(totalFinal);
        nuevaVenta.setEstado("completada");
        
        // Guardar anticipo si es credito
        if (metodo.equals("Crédito")) {
            double anticipo = 0;
            try { anticipo = Double.parseDouble(etAnticipo.getText().toString()); } catch (Exception ignored) {}
            
            // Registro de abono inicial
            Abono abono = new Abono();
            abono.setClienteCedula(selectedCliente.getCedula());
            abono.setMonto(anticipo);
            abono.setFecha(fechaDia);
            abono.setMetodoPago("efectivo");
            abono.setObservacion("Anticipo inicial de venta a crédito");
            abono.setFechaRegistro(fechaDia + " " + fechaHora);
            
            // Guardar abono
            com.agrovet.pos.database.AppDatabase.databaseWriteExecutor.execute(() -> {
                com.agrovet.pos.database.AppDatabase.getDatabase(this).abonoDao().insert(abono);
            });
        }

        ventaViewModel.addVenta(nuevaVenta);
        
        // Bajar stock
        for (CartItem item : cartList) {
            Producto p = item.getProducto();
            p.setCantidad(p.getCantidad() - item.getCantidad());
            productoViewModel.updateProducto(p);
        }

        AppLogger.i("Venta guardada: " + totalFinal + " (" + metodo + ")");
        Toast.makeText(this, "Venta Finalizada Exitosamente", Toast.LENGTH_LONG).show();
        finish();
    }

    private void loadCatalog() {
        productoViewModel.getProductos().observe(this, productos -> {
            if (productos != null) {
                catalogListFull.clear();
                catalogListFull.addAll(productos);
                filterCatalog("");
            }
        });
    }

    private void loadClientes() {
        clienteViewModel.getClientes().observe(this, clientes -> {
            if (clientes != null) {
                clientesList.clear();
                clientesList.addAll(clientes);
                
                // Usamos un adaptador personalizado para que muestre el nombre del cliente
                ArrayAdapter<Cliente> adapter = new ArrayAdapter<Cliente>(this, android.R.layout.simple_dropdown_item_1line, clientesList) {
                    @Override
                    public View getView(int position, View convertView, android.view.ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);
                        if (v instanceof TextView) {
                            ((TextView) v).setText(getItem(position).getNombre());
                        }
                        return v;
                    }
                };
                etBuscarCliente.setAdapter(adapter);
                
                // Configurar el umbral para que empiece a sugerir desde el primer caracter
                etBuscarCliente.setThreshold(1);
            }
        });
    }

    private void filterCatalog(String query) {
        catalogList.clear();
        if (query.isEmpty()) {
            catalogList.addAll(catalogListFull);
        } else {
            for (Producto p : catalogListFull) {
                if (p.getNombre().toLowerCase().contains(query.toLowerCase())) {
                    catalogList.add(p);
                }
            }
        }
        catalogAdapter.notifyDataSetChanged();
    }

    private void addToCart(Producto producto) {
        if (producto.getStock() <= 0) {
            Toast.makeText(this, "Producto sin stock", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean found = false;
        for (CartItem item : cartList) {
            if (item.getProducto().getId().equals(producto.getId())) {
                if (item.getCantidad() + 1 > producto.getStock()) {
                    Toast.makeText(this, "No hay más stock disponible", Toast.LENGTH_SHORT).show();
                    return;
                }
                item.setCantidad(item.getCantidad() + 1);
                found = true;
                break;
            }
        }

        if (!found) {
            cartList.add(new CartItem(producto, 1));
        }

        Toast.makeText(this, producto.getNombre() + " añadido", Toast.LENGTH_SHORT).show();
        updateTotals();
        cartAdapter.notifyDataSetChanged();
    }

    private void updateTotals() {
        subtotalVenta = 0;
        for (CartItem item : cartList) {
            subtotalVenta += item.getTotal();
        }

        double descuento = 0;
        try { descuento = Double.parseDouble(etDescuento.getText().toString()); } catch (Exception ignored) {}
        
        totalFinal = subtotalVenta - descuento;
        if (totalFinal < 0) totalFinal = 0;

        double montoRecibido = 0;
        try { montoRecibido = Double.parseDouble(etMontoRecibido.getText().toString()); } catch (Exception ignored) {}
        
        double vuelto = montoRecibido - totalFinal;
        if (vuelto < 0) vuelto = 0;

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        txtTotal.setText(format.format(totalFinal));
        txtResumenSubtotal.setText(format.format(subtotalVenta));
        txtVuelto.setText(format.format(vuelto));
    }
}
