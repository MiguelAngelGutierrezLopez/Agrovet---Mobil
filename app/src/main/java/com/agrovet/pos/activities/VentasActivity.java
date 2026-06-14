package com.agrovet.pos.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.agrovet.pos.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.chip.Chip;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.text.Normalizer;
import com.agrovet.pos.adapters.CartAdapter;
import com.agrovet.pos.adapters.ProductoAdapter;
import com.agrovet.pos.models.Abono;
import com.agrovet.pos.models.CartItem;
import com.agrovet.pos.models.Cliente;
import com.agrovet.pos.models.Movimiento;
import com.agrovet.pos.models.Producto;
import com.agrovet.pos.models.Venta;
import com.agrovet.pos.utils.AppLogger;
import com.agrovet.pos.viewmodels.ClienteViewModel;
import com.agrovet.pos.viewmodels.MovimientoViewModel;
import com.agrovet.pos.viewmodels.ProductoViewModel;
import com.agrovet.pos.viewmodels.VentaViewModel;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VentasActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView rvCatalog, rvCart;
    private EditText etBuscar, etDescuento, etMontoRecibido, etAnticipo, etDiasCredito;
    private AutoCompleteTextView etBuscarCliente;
    private TextView txtTotal, txtResumenSubtotal, txtStep1Indicator, txtStep2Indicator, txtVuelto;
    private Button btnFinalizar, btnNextStep, btnBackStep, btnVentaEspecifica;
    private MaterialCardView cardContado, cardCredito, cardBanco;
    private RadioButton rbContado, rbCredito, rbBanco;
    private ChipGroup cgSubmetodoBanco;
    private View layoutStep1, layoutStep2, layoutAnticipo, layoutBancoOptions;
    
    private ProductoViewModel productoViewModel;
    private VentaViewModel ventaViewModel;
    private ClienteViewModel clienteViewModel;
    private MovimientoViewModel movimientoViewModel;
    
    private ProductoAdapter catalogAdapter;
    private CartAdapter cartAdapter;
    
    private final List<Producto> catalogList = new ArrayList<>();
    private final List<Producto> catalogListFull = new ArrayList<>();
    private final List<CartItem> cartList = new ArrayList<>();
    private final List<Cliente> clientesList = new ArrayList<>();

    private boolean isVentaEspecifica = false;
    private Cliente selectedCliente = null;
    private double subtotalVenta = 0;
    private double totalFinal = 0;
    private String metodoSeleccionado = "Contado";
    
    private String[] CATEGORIAS = {
            "ANTIBIOTICOS", "BIOESTIMULANTES", "BIOLOGICOS", "COADYUVANTES", "CONCENTRADOS",
            "CONCENTRADO AVES PRODUCCION", "CONCENTRADOS GATOS", "CONCENTRADOS PERROS", "ENMIENDA",
            "FERTILIZANTES", "FUNGICIDAS", "HERBICIDAS", "INSECTICIDAS", "MAIZ", "MASCOTAS",
            "REGULADOR DE CRECIMIENTO", "REPUESTOS, BOMBAS Y ESTACIONARIAS", "SALES GANADERAS",
            "SEMILLAS", "VETERINARIA"
    };

    private ChipGroup cgFiltroCategorias;
    private String categoriaSeleccionada = "TODOS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas);

        productoViewModel = new ViewModelProvider(this).get(ProductoViewModel.class);
        ventaViewModel = new ViewModelProvider(this).get(VentaViewModel.class);
        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
        movimientoViewModel = new ViewModelProvider(this).get(MovimientoViewModel.class);

        initViews();
        setupDrawer();
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
        etDiasCredito = findViewById(R.id.et_dias_credito);
        
        txtTotal = findViewById(R.id.txt_total_venta);
        txtResumenSubtotal = findViewById(R.id.txt_resumen_subtotal);
        txtStep1Indicator = findViewById(R.id.step1_indicator);
        txtStep2Indicator = findViewById(R.id.step2_indicator);
        txtVuelto = findViewById(R.id.txt_vuelto);
        
        btnNextStep = findViewById(R.id.btn_next_step);
        btnBackStep = findViewById(R.id.btn_back_to_step1);
        btnFinalizar = findViewById(R.id.btn_finalizar_venta);
        btnVentaEspecifica = findViewById(R.id.btn_venta_especifica);
        
        cardContado = findViewById(R.id.card_contado);
        cardCredito = findViewById(R.id.card_credito);
        cardBanco = findViewById(R.id.card_banco);
        
        rbContado = findViewById(R.id.rb_contado);
        rbCredito = findViewById(R.id.rb_credito);
        rbBanco = findViewById(R.id.rb_banco);
        
        cgSubmetodoBanco = findViewById(R.id.cg_submetodo_banco);
        
        layoutStep1 = findViewById(R.id.layout_step1);
        layoutStep2 = findViewById(R.id.layout_step2);
        layoutAnticipo = findViewById(R.id.layout_anticipo);
        layoutBancoOptions = findViewById(R.id.layout_banco_options);

        cgFiltroCategorias = findViewById(R.id.cg_filtro_categorias);
        setupFiltroCategorias();
    }

    private void setupFiltroCategorias() {
        if (cgFiltroCategorias == null) return;
        
        // Opción para ver todos
        Chip chipTodos = new Chip(this);
        chipTodos.setText("TODOS");
        chipTodos.setCheckable(true);
        chipTodos.setChecked(true);
        cgFiltroCategorias.addView(chipTodos);

        for (String cat : CATEGORIAS) {
            Chip chip = new Chip(this);
            chip.setText(cat);
            chip.setCheckable(true);
            cgFiltroCategorias.addView(chip);
        }

        cgFiltroCategorias.setOnCheckedChangeListener((group, checkedId) -> {
            Chip selectedChip = group.findViewById(checkedId);
            if (selectedChip != null) {
                categoriaSeleccionada = selectedChip.getText().toString();
            } else {
                categoriaSeleccionada = "TODOS";
            }
            filterCatalog(etBuscar.getText().toString());
        });
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
        // Simplificamos la acción: cualquier clic en la tarjeta agrega al carrito
        catalogAdapter.setOnItemClickListener(this::addToCart);
        
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
            // El convertResultToString del filtro ya se encarga de poner el nombre en el texto
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

        cardContado.setOnClickListener(v -> selectPaymentMethod("Contado"));
        cardCredito.setOnClickListener(v -> selectPaymentMethod("Crédito"));
        cardBanco.setOnClickListener(v -> selectPaymentMethod("Banco"));
        
        rbContado.setOnClickListener(v -> selectPaymentMethod("Contado"));
        rbCredito.setOnClickListener(v -> selectPaymentMethod("Crédito"));
        rbBanco.setOnClickListener(v -> selectPaymentMethod("Banco"));

        btnFinalizar.setOnClickListener(v -> {
            try {
                guardarVenta();
            } catch (Exception e) {
                AppLogger.e("Error al finalizar venta", e);
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectPaymentMethod(String metodo) {
        if ("Crédito".equals(metodo)) {
            if (!isVentaEspecifica || selectedCliente == null) {
                Toast.makeText(this, "Crédito solo disponible para clientes específicos", Toast.LENGTH_LONG).show();
                return;
            }
        }

        metodoSeleccionado = metodo;
        
        // Update selection UI
        rbContado.setChecked("Contado".equals(metodo));
        rbCredito.setChecked("Crédito".equals(metodo));
        rbBanco.setChecked("Banco".equals(metodo));
        
        rbContado.setTextColor(getColor("Contado".equals(metodo) ? R.color.teal : R.color.gris_oscuro));
        rbCredito.setTextColor(getColor("Crédito".equals(metodo) ? R.color.teal : R.color.gris_oscuro));
        rbBanco.setTextColor(getColor("Banco".equals(metodo) ? R.color.teal : R.color.gris_oscuro));
        
        // Update Card Styles
        cardContado.setStrokeColor(getColor("Contado".equals(metodo) ? R.color.teal : R.color.gris_claro));
        cardContado.setStrokeWidth("Contado".equals(metodo) ? 4 : 2);
        
        cardCredito.setStrokeColor(getColor("Crédito".equals(metodo) ? R.color.teal : R.color.gris_claro));
        cardCredito.setStrokeWidth("Crédito".equals(metodo) ? 4 : 2);
        
        cardBanco.setStrokeColor(getColor("Banco".equals(metodo) ? R.color.teal : R.color.gris_claro));
        cardBanco.setStrokeWidth("Banco".equals(metodo) ? 4 : 2);
        
        // Update Layout visibility
        layoutAnticipo.setVisibility("Crédito".equals(metodo) ? View.VISIBLE : View.GONE);
        layoutBancoOptions.setVisibility("Banco".equals(metodo) ? View.VISIBLE : View.GONE);
        
        if ("Banco".equals(metodo)) {
            // Default to Nequi if nothing selected
            if (cgSubmetodoBanco.getCheckedChipId() == View.NO_ID) {
                cgSubmetodoBanco.check(R.id.chip_nequi);
            }
        }
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
        
        String metodo = metodoSeleccionado;
        String submetodo = null;
        
        if ("Banco".equals(metodo)) {
            int subId = cgSubmetodoBanco.getCheckedChipId();
            if (subId == R.id.chip_nequi) submetodo = "Nequi";
            else if (subId == R.id.chip_transaccion) submetodo = "Transacción";
            else if (subId == R.id.chip_tarjeta) submetodo = "Tarjeta";
        }

        double descuento = 0;
        try { descuento = Double.parseDouble(etDescuento.getText().toString()); } catch (Exception ignored) {}

        double anticipoVal = 0;
        int diasCreditoVal = 30;
        if ("Crédito".equals(metodo)) {
            try { anticipoVal = Double.parseDouble(etAnticipo.getText().toString()); } catch (Exception ignored) {}
            try { diasCreditoVal = Integer.parseInt(etDiasCredito.getText().toString()); } catch (Exception ignored) {}
        }

        Venta nuevaVenta = new Venta();
        nuevaVenta.setFechaDia(fechaDia);
        nuevaVenta.setFechaHora(fechaHora);
        nuevaVenta.setNombreCliente(selectedCliente != null ? selectedCliente.getNombre() : "Cliente Final");
        nuevaVenta.setClienteCedula(selectedCliente != null ? selectedCliente.getCedula() : null);
        nuevaVenta.setTipoPago(metodo);
        nuevaVenta.setSubmetodoBanco(submetodo);
        nuevaVenta.setSubtotal(subtotalVenta);
        nuevaVenta.setDescuento(descuento);
        nuevaVenta.setTotal(totalFinal);
        nuevaVenta.setAnticipo(anticipoVal);
        nuevaVenta.setDiasCredito(diasCreditoVal);
        nuevaVenta.setEstado("completada");
        
        // Si es credito, guardar anticipo en abonos
        if (metodo.equals("Crédito")) {
            Abono abono = new Abono();
            abono.setClienteCedula(selectedCliente.getCedula());
            abono.setMonto(anticipoVal);
            abono.setFecha(fechaDia);
            abono.setMetodoPago("efectivo");
            abono.setObservacion("Anticipo inicial de venta a crédito");
            abono.setFechaRegistro(fechaDia + " " + fechaHora);
            
            com.agrovet.pos.database.AppDatabase.databaseWriteExecutor.execute(() -> {
                com.agrovet.pos.database.AppDatabase.getDatabase(this).abonoDao().insert(abono);
            });
        }

        // Sincronizamos las listas después de guardar la venta
        Venta finalVenta = nuevaVenta;
        ventaViewModel.addVenta(finalVenta, cartList);
        
        // Actualizar stock de productos localmente
        for (CartItem item : cartList) {
            Producto p = item.getProducto();
            p.setCantidad(p.getCantidad() - item.getCantidad());
            productoViewModel.updateProducto(p);
        }

        // Registrar movimiento de caja localmente
        // Ingreso real: anticipo si es crédito, total si es banco o contado
        double ingresoRealCaja = ("Crédito".equals(metodo)) ? anticipoVal : totalFinal;

        Movimiento mov = new Movimiento();
        mov.setIngresos(ingresoRealCaja);
        
        java.text.NumberFormat curFormat = java.text.NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        String razon = "Venta " + ("Crédito".equals(metodo) ? "Crédito (Abono)" : "Contado") + 
                      " - " + finalVenta.getNombreCliente() + 
                      " [Total: " + curFormat.format(totalFinal) + "]";
        
        mov.setRazonIngreso(razon);
        mov.setFechaIngreso(fechaDia + " " + fechaHora);
        mov.setCategoria("Venta de productos");
        
        // IMPORTANTE: Marcamos como venta y sincronizado para evitar duplicidad en el servidor
        mov.setVenta(true);
        mov.setSynced(true); 

        if (movimientoViewModel != null) {
            movimientoViewModel.addMovimiento(mov);
        }

        AppLogger.i("Venta corporativa guardada: " + totalFinal + " (" + metodo + ")");
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
                
                // Adaptador que solo muestra el nombre del cliente en la lista
                ArrayAdapter<Cliente> adapter = new ArrayAdapter<Cliente>(this, android.R.layout.simple_dropdown_item_1line, clientesList) {
                    @NonNull
                    @Override
                    public View getView(int position, View convertView, @NonNull android.view.ViewGroup parent) {
                        TextView tv = (TextView) super.getView(position, convertView, parent);
                        tv.setText(getItem(position).getNombre());
                        tv.setTextColor(getColor(R.color.gris_oscuro));
                        return tv;
                    }

                    @NonNull
                    @Override
                    public Filter getFilter() {
                        return new Filter() {
                            @Override
                            protected FilterResults performFiltering(CharSequence constraint) {
                                FilterResults results = new FilterResults();
                                List<Cliente> suggestions = new ArrayList<>();
                                if (constraint != null) {
                                    String filterPattern = constraint.toString().toLowerCase().trim();
                                    for (Cliente c : clientesList) {
                                        if (c.getNombre().toLowerCase().contains(filterPattern)) {
                                            suggestions.add(c);
                                        }
                                    }
                                }
                                results.values = suggestions;
                                results.count = suggestions.size();
                                return results;
                            }

                            @Override
                            protected void publishResults(CharSequence constraint, FilterResults results) {
                                clear();
                                if (results != null && results.count > 0) {
                                    addAll((List<Cliente>) results.values);
                                }
                                notifyDataSetChanged();
                            }

                            @Override
                            public CharSequence convertResultToString(Object resultValue) {
                                return ((Cliente) resultValue).getNombre();
                            }
                        };
                    }
                };
                etBuscarCliente.setAdapter(adapter);
                etBuscarCliente.setThreshold(1);

                // Forzar mostrar sugerencias al tocar el campo
                etBuscarCliente.setOnClickListener(v -> {
                    if (etBuscarCliente.getText().toString().isEmpty()) {
                        etBuscarCliente.showDropDown();
                    }
                });
            }
        });
    }

    private void filterCatalog(String query) {
        catalogList.clear();
        String queryLower = query.toLowerCase().trim();
        
        for (Producto p : catalogListFull) {
            boolean matchQuery = queryLower.isEmpty() || p.getNombre().toLowerCase().contains(queryLower);
            
            String catProducto = p.getCategoria() != null ? p.getCategoria().toUpperCase() : "";
            // Normalizar categoría del producto para comparar
            catProducto = Normalizer.normalize(catProducto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
            
            boolean matchCat = categoriaSeleccionada.equals("TODOS") || catProducto.equals(categoriaSeleccionada);
            
            if (matchQuery && matchCat) {
                catalogList.add(p);
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
