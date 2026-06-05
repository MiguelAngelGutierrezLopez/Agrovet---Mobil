package com.agrovet.pos.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.agrovet.pos.models.CartItem;
import com.agrovet.pos.models.Producto;
import com.agrovet.pos.models.Venta;
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
    private EditText etBuscar;
    private TextView txtTotal, txtTicket;
    private Button btnNormal, btnMixta, btnFinalizar, btnCancelar;
    
    private ProductoViewModel productoViewModel;
    private VentaViewModel ventaViewModel;
    private ProductoAdapter catalogAdapter;
    private CartAdapter cartAdapter;
    
    private final List<Producto> catalogList = new ArrayList<>();
    private final List<Producto> catalogListFull = new ArrayList<>();
    private final List<CartItem> cartList = new ArrayList<>();
    
    private double totalVenta = 0;
    private String tipoPago = "Contado";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas);

        productoViewModel = new ViewModelProvider(this).get(ProductoViewModel.class);
        ventaViewModel = new ViewModelProvider(this).get(VentaViewModel.class);

        initViews();
        setupToolbar();
        setupRecyclerViews();
        setupListeners();
        loadCatalog();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvCatalog = findViewById(R.id.rv_productos_disponibles);
        rvCart = findViewById(R.id.rv_carrito);
        etBuscar = findViewById(R.id.et_buscar_producto);
        txtTotal = findViewById(R.id.txt_total_venta);
        txtTicket = findViewById(R.id.txt_ticket_numero);
        btnNormal = findViewById(R.id.btn_venta_normal);
        btnMixta = findViewById(R.id.btn_venta_mixta);
        btnFinalizar = findViewById(R.id.btn_finalizar_venta);
        btnCancelar = findViewById(R.id.btn_cancelar_venta);
        
        txtTicket.setText("#0016");
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            getSupportActionBar().setTitle(R.string.menu_ventas);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerViews() {
        catalogAdapter = new ProductoAdapter(catalogList, new ProductoAdapter.OnProductoActionListener() {
            @Override
            public void onEditar(Producto producto) {
                addToCart(producto);
            }
            @Override
            public void onEliminar(Producto producto) {
                addToCart(producto);
            }
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
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCatalog(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnNormal.setOnClickListener(v -> {
            tipoPago = "Contado";
            btnNormal.setBackgroundTintList(getColorStateList(R.color.teal));
            btnNormal.setTextColor(getColor(R.color.white));
            btnMixta.setBackgroundTintList(getColorStateList(R.color.white));
            btnMixta.setTextColor(getColor(R.color.gris_oscuro));
        });

        btnMixta.setOnClickListener(v -> {
            tipoPago = "Mixta";
            btnMixta.setBackgroundTintList(getColorStateList(R.color.teal));
            btnMixta.setTextColor(getColor(R.color.white));
            btnNormal.setBackgroundTintList(getColorStateList(R.color.white));
            btnNormal.setTextColor(getColor(R.color.gris_oscuro));
        });

        btnFinalizar.setOnClickListener(v -> {
            if (cartList.isEmpty()) {
                Toast.makeText(this, "El carrito esta vacio", Toast.LENGTH_SHORT).show();
                return;
            }
            guardarVenta();
        });

        btnCancelar.setOnClickListener(v -> finish());
    }

    private void guardarVenta() {
        String fechaDia = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String fechaHora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        
        Venta nuevaVenta = new Venta();
        nuevaVenta.setFechaDia(fechaDia);
        nuevaVenta.setFechaHora(fechaHora);
        nuevaVenta.setNombreCliente("Cliente Final");
        nuevaVenta.setTipoPago(tipoPago);
        nuevaVenta.setSubtotal(totalVenta);
        nuevaVenta.setTotal(totalVenta);
        nuevaVenta.setEstado("completada");
        
        ventaViewModel.addVenta(nuevaVenta);
        
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
            if (item.getProducto().getId() == producto.getId()) {
                item.setCantidad(item.getCantidad() + 1);
                found = true;
                break;
            }
        }

        if (!found) {
            cartList.add(new CartItem(producto, 1));
        }

        updateTotals();
        cartAdapter.notifyDataSetChanged();
    }

    private void updateTotals() {
        totalVenta = 0;
        for (CartItem item : cartList) {
            totalVenta += item.getTotal();
        }
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        txtTotal.setText(format.format(totalVenta));
    }
}
