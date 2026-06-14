package com.agrovet.pos.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import com.agrovet.pos.AgrovetApplication;
import com.agrovet.pos.database.AppDatabase;
import com.agrovet.pos.models.*;
import com.agrovet.pos.network.*;
import com.agrovet.pos.network.dto.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Response;

public class SyncManager {
    private final AppDatabase db;
    private final SalesApiService salesApi;
    private final InventoryApiService inventoryApi;
    private final UserApiService userApi;
    private final ReportApiService reportApi;
    private final Context context;

    public SyncManager(Context context) {
        this.context = context;
        this.db = ((AgrovetApplication) context.getApplicationContext()).getDatabase();
        this.salesApi = RetrofitClient.getVentasClient().create(SalesApiService.class);
        this.inventoryApi = RetrofitClient.getInventarioClient().create(InventoryApiService.class);
        this.userApi = RetrofitClient.getUsuariosClient().create(UserApiService.class);
        this.reportApi = RetrofitClient.getReportesClient().create(ReportApiService.class);
    }

    public interface SyncCallback {
        void onSuccess(String summary);
        void onError(String message);
        void onProgress(String status);
    }

    private boolean isServerReachable() {
        AppLogger.i("Verificando conectividad de red...");
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities == null || !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                AppLogger.w("No hay conexión a internet activa.");
                return false;
            }
        }

        AppLogger.i("Probando resolución DNS del servidor...");
        try {
            InetAddress address = InetAddress.getByName(Constants.DOMAIN_USUARIOS);
            boolean reachable = address != null && !address.getHostAddress().isEmpty();
            if (reachable) {
                AppLogger.i("Conexión exitosa con el servidor: " + Constants.DOMAIN_USUARIOS);
            } else {
                AppLogger.w("No se pudo resolver la dirección del servidor.");
            }
            return reachable;
        } catch (Exception e) {
            AppLogger.e("Fallo de conexión con el servidor", e);
            return false;
        }
    }

    public void getSyncSummary(SyncCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppLogger.d("Calculando resumen de sincronización local...");
            List<Venta> sales = db.ventaDao().getUnsyncedVentas();
            List<Producto> products = db.productoDao().getUnsyncedProductos();
            List<Cliente> clients = db.clienteDao().getUnsyncedClientes();
            List<Movimiento> movements = db.movimientoDao().getUnsyncedMovimientos();

            String summary = "Pendiente por subir:\n" +
                    "• Ventas: " + sales.size() + "\n" +
                    "• Clientes: " + clients.size() + "\n" +
                    "• Productos: " + products.size() + "\n" +
                    "• Proveedores: " + db.proveedorDao().getUnsyncedProveedores().size() + "\n" +
                    "• Movimientos: " + movements.size();
            
            AppLogger.i("Resumen local: " + sales.size() + " ventas, " + clients.size() + " clientes.");
            callback.onSuccess(summary);
        });
    }

    public void pullNewData(SyncCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppLogger.i("--- INICIANDO PULL (RECIBIR DATOS) ---");
            if (!isServerReachable()) {
                callback.onError("No hay conexión con el servidor. Verifique su internet.");
                return;
            }

            try {
                int totalNewItems = 0;
                StringBuilder summary = new StringBuilder("Datos recibidos del servidor:\n");

                // 1. Clientes
                AppLogger.d("PULL: Solicitando clientes...");
                Response<ClientSyncResponse> respCli = userApi.getSyncClientes(null).execute();
                if (respCli.isSuccessful() && respCli.body() != null && respCli.body().getClientes() != null) {
                    List<Cliente> clientes = respCli.body().getClientes();
                    AppLogger.i("PULL: Recibidos " + clientes.size() + " clientes.");
                    for (Cliente c : clientes) {
                        c.setSynced(true);
                        db.clienteDao().insert(c);
                        totalNewItems++;
                    }
                    summary.append("• ").append(clientes.size()).append(" Clientes\n");
                }

                // 2. Productos
                Integer lastProdId = db.productoDao().getMaxServerId();
                AppLogger.d("PULL: Solicitando productos...");
                Response<ProductSyncResponse> respProd = inventoryApi.getSyncProductos(lastProdId).execute();
                if (respProd.isSuccessful() && respProd.body() != null && respProd.body().getProductos() != null) {
                    List<Producto> productos = respProd.body().getProductos();
                    AppLogger.i("PULL: Recibidos " + productos.size() + " productos.");
                    for (Producto p : productos) {
                        p.setSynced(true);
                        Producto existing = db.productoDao().findByServerId(p.getServerId());
                        if (existing != null) {
                            p.setId(existing.getId());
                            db.productoDao().update(p);
                        } else {
                            db.productoDao().insertOrIgnore(p);
                        }
                        totalNewItems++;
                    }
                    summary.append("• ").append(productos.size()).append(" Productos\n");
                }

                // 3. Movimientos
                Integer lastMovId = db.movimientoDao().getMaxServerId();
                AppLogger.d("PULL: Solicitando movimientos...");
                Response<MovementSyncResponse> respMov = reportApi.syncMovimientos(lastMovId).execute();
                if (respMov.isSuccessful() && respMov.body() != null && respMov.body().getData() != null && respMov.body().getData().getMovimientos() != null) {
                    List<Movimiento> movimientos = respMov.body().getData().getMovimientos();
                    AppLogger.i("PULL: Recibidos " + movimientos.size() + " movimientos.");
                    for (Movimiento m : movimientos) {
                        m.setSynced(true);
                        Movimiento existing = db.movimientoDao().findByServerId(m.getServerId());
                        if (existing != null) {
                            m.setId(existing.getId());
                            db.movimientoDao().update(m);
                        } else {
                            db.movimientoDao().insertOrIgnore(m);
                        }
                        totalNewItems++;
                    }
                    summary.append("• ").append(movimientos.size()).append(" Movimientos\n");
                }

                // 4. Ventas (Carga Historial Completo)
                AppLogger.d("PULL: Solicitando historial completo de ventas...");
                Response<SaleSyncResponse> respVenta = salesApi.getHistorialVentas().execute();
                if (respVenta.isSuccessful() && respVenta.body() != null && respVenta.body().getVentas() != null) {
                    List<Venta> ventas = respVenta.body().getVentas();
                    AppLogger.i("PULL: Recibidas " + ventas.size() + " ventas del historial.");
                    for (Venta v : ventas) {
                        v.setSynced(true);
                        // El servidor devuelve 'id' como el ID de base de datos remoto
                        Venta existing = db.ventaDao().findByServerId(v.getServerId());
                        if (existing != null) {
                            v.setId(existing.getId());
                            db.ventaDao().update(v);
                        } else {
                            db.ventaDao().insertOrIgnore(v);
                        }
                        totalNewItems++;
                    }
                    summary.append("• ").append(ventas.size()).append(" Ventas (Historial)\n");
                }

                // 5. Proveedores (Pull Completo)
                AppLogger.d("PULL: Solicitando proveedores...");
                Response<Map<String, Object>> respProv = userApi.getProveedores().execute();
                if (respProv.isSuccessful() && respProv.body() != null && Boolean.TRUE.equals(respProv.body().get("success"))) {
                    List<Map<String, Object>> provsRaw = (List<Map<String, Object>>) respProv.body().get("proveedores");
                    if (provsRaw != null) {
                        AppLogger.i("PULL: Recibidos " + provsRaw.size() + " proveedores.");
                        for (Map<String, Object> map : provsRaw) {
                            Proveedor p = new Proveedor();
                            p.setTelefono(String.valueOf(map.get("telefono")));
                            p.setNombreEmpresa(String.valueOf(map.get("nombre_empresa")));
                            p.setNombreProveedor(String.valueOf(map.get("nombre_proveedor")));
                            p.setCorreo(map.get("correo") != null ? String.valueOf(map.get("correo")) : "");
                            p.setEstado(map.get("estado") != null ? String.valueOf(map.get("estado")) : "activo");
                            p.setSynced(true);
                            db.proveedorDao().insert(p);
                        }
                        summary.append("• ").append(provsRaw.size()).append(" Proveedores\n");
                    }
                }

                AppLogger.i("--- PULL FINALIZADO. Total items: " + totalNewItems + " ---");
                if (totalNewItems > 0) callback.onSuccess(summary.toString());
                else callback.onSuccess("No se encontraron cambios nuevos en el servidor.");

            } catch (Exception e) {
                AppLogger.e("ERROR CRÍTICO EN PULL", e);
                callback.onError("Fallo al recibir datos: " + e.getMessage());
            }
        });
    }

    public void sendDataToWeb(SyncCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppLogger.i("--- INICIANDO PUSH (MANDAR DATOS) ---");
            if (!isServerReachable()) {
                callback.onError("No hay conexión con el servidor.");
                return;
            }

            try {
                int count = 0;
                
                // 1. Clientes
                List<Cliente> clients = db.clienteDao().getUnsyncedClientes();
                for (Cliente c : clients) {
                    callback.onProgress("Subiendo cliente: " + c.getNombre());
                    ClienteRequest req = new ClienteRequest();
                    req.setCedula(c.getCedula());
                    req.setNombre(c.getNombre());
                    req.setTelefono(c.getTelefono());
                    req.setCorreo(c.getCorreo());
                    req.setDireccion(c.getDireccion());
                    
                    Response<GenericResponse> resp = userApi.createCliente(req).execute();
                    if (resp.isSuccessful()) {
                        AppLogger.i("Cliente " + c.getCedula() + " subido con éxito.");
                        c.setSynced(true);
                        // El servidor de clientes devuelve 'cedula' en lugar de un ID autoincremental
                        db.clienteDao().update(c);
                        count++;
                    } else {
                        String errorBody = "";
                        try { if (resp.errorBody() != null) errorBody = resp.errorBody().string(); } catch (Exception ignored) {}
                        AppLogger.e("Fallo al subir cliente " + c.getCedula() + ". Error: " + resp.code() + " " + errorBody, null);
                    }
                }

                // 2. Productos
                List<Producto> products = db.productoDao().getUnsyncedProductos();
                for (Producto p : products) {
                    callback.onProgress("Subiendo producto: " + p.getNombre());
                    ProductoRequest req = new ProductoRequest();
                    req.setNombre(p.getNombre());
                    req.setDescripcion(p.getDescripcion());
                    req.setCategoria(p.getCategoria());
                    req.setCantidad(p.getCantidad());
                    req.setPresentacion(p.getPresentacion());
                    req.setPrecioVenta(p.getPrecioVenta());
                    req.setPrecioCosto(p.getPrecioCosto());
                    
                    // Si el proveedor está vacío o es nulo, enviamos null para que GSON lo omita/envíe null
                    String prov = p.getProveedor();
                    if (prov == null || prov.trim().isEmpty()) {
                        req.setProveedor(null);
                    } else {
                        req.setProveedor(prov);
                    }
                    
                    Response<GenericResponse> resp = inventoryApi.createProducto(req).execute();
                    if (resp.isSuccessful() && resp.body() != null) {
                        Integer sid = resp.body().getProductoId();
                        if (sid == null) sid = resp.body().getId(); // Probar con el campo 'id' raíz
                        if (sid == null && resp.body().getData() != null) {
                            sid = resp.body().getData().getId();
                        }

                        if (sid != null) {
                            AppLogger.i("Producto " + p.getNombre() + " subido con ID: " + sid);
                            p.setSynced(true);
                            p.setServerId(sid);
                            db.productoDao().update(p);
                            count++;
                        } else {
                            // Si el servidor no devuelve el ID, pero fue exitoso, igual lo marcamos como sincronizado
                            // para evitar bucles, o al menos incrementamos el contador para informar al usuario.
                            // Nota: Es mejor que el servidor devuelva el ID para futuras actualizaciones.
                            AppLogger.w("Producto " + p.getNombre() + " subido (200 OK) pero el servidor no envió el ID.");
                            p.setSynced(true);
                            db.productoDao().update(p);
                            count++;
                        }
                    } else {
                        String errorBody = "";
                        try { if (resp.errorBody() != null) errorBody = resp.errorBody().string(); } catch (Exception ignored) {}
                        AppLogger.e("Fallo al subir producto " + p.getNombre() + ". Error: " + resp.code() + " " + errorBody, null);
                    }
                }

                // 3. Movimientos
                List<Movimiento> movements = db.movimientoDao().getUnsyncedMovimientos();
                for (Movimiento m : movements) {
                    // Solo enviar si tiene un monto válido (> 0) o es un ingreso/egreso real
                    if (m.getMonto() <= 0) {
                        AppLogger.d("Omitiendo movimiento con monto 0: " + m.getRazon());
                        m.setSynced(true);
                        db.movimientoDao().update(m);
                        continue;
                    }

                    callback.onProgress("Subiendo movimiento: " + m.getRazon());
                    MovimientoRequest req = new MovimientoRequest();
                    req.setTipo(m.getTipo().toLowerCase());
                    req.setMonto(m.getMonto());
                    req.setRazon(m.getRazon());
                    req.setCategoria(m.getCategoria());
                    
                    Response<GenericResponse> resp = reportApi.createMovimiento(req).execute();
                    if (resp.isSuccessful() && resp.body() != null) {
                        AppLogger.i("Movimiento subido.");
                        m.setSynced(true);
                        if (resp.body().getData() != null) {
                            m.setServerId(resp.body().getData().getId());
                        }
                        db.movimientoDao().update(m);
                        count++;
                    } else {
                        AppLogger.e("Fallo al subir movimiento. Error: " + resp.code(), null);
                    }
                }

                // 4. Ventas
                List<Venta> sales = db.ventaDao().getUnsyncedVentas();
                for (Venta v : sales) {
                    callback.onProgress("Subiendo venta #" + v.getId());
                    VentaRequest req = new VentaRequest();
                    req.setSubtotal(v.getSubtotal());
                    req.setDescuento(v.getDescuento());
                    req.setTotal(v.getTotal());
                    String metodoNormalizado = v.getTipoPago().toLowerCase();
                    if (metodoNormalizado.contains("crédito")) metodoNormalizado = "credito";
                    
                    req.setMetodoPago(metodoNormalizado);
                    req.setClienteCedula(v.getClienteCedula() != null ? v.getClienteCedula() : "final");
                    req.setDineroEntregado(v.getTotal());
                    req.setEsMixta(false);
                    
                    if ("credito".equalsIgnoreCase(req.getMetodoPago())) {
                        req.setDiasCredito(v.getDiasCredito() != null ? v.getDiasCredito() : 30);
                        req.setAnticipo(v.getAnticipo());
                    }

                    List<VentaItem> dbItems = db.ventaItemDao().getItemsByVenta(v.getId());
                    List<VentaRequest.ProductoItem> requestItems = new ArrayList<>();
                    boolean allProductsSynced = true;
                    for (VentaItem item : dbItems) {
                        // Buscar el producto para obtener su server_id
                        Producto p = db.productoDao().getProductoById(item.getProductoId());
                        if (p == null || p.getServerId() == null) {
                            AppLogger.w("Pospuesta venta #" + v.getId() + " porque el producto " + item.getNombreProducto() + " no tiene ID de servidor.");
                            allProductsSynced = false;
                            break;
                        }
                        
                        requestItems.add(new VentaRequest.ProductoItem(
                                p.getServerId(),
                                item.getNombreProducto(),
                                item.getCantidad(),
                                item.getPrecioUnitario(),
                                item.getTotal()
                        ));
                    }

                    if (!allProductsSynced) {
                        continue;
                    }

                    req.setProductos(requestItems);

                    Response<GenericResponse> resp = salesApi.createVenta(req).execute();
                    if (resp.isSuccessful() && resp.body() != null) {
                        AppLogger.i("Venta #" + v.getId() + " subida (ID: " + resp.body().getVentaId() + ")");
                        v.setSynced(true);
                        v.setServerId(resp.body().getVentaId());
                        v.setNumeroVenta(resp.body().getTicketNumero());
                        db.ventaDao().update(v);
                        count++;
                    } else {
                        AppLogger.e("Fallo al subir venta #" + v.getId() + ". Error: " + resp.code(), null);
                    }
                }

                // 5. Proveedores
                List<Proveedor> providers = db.proveedorDao().getUnsyncedProveedores();
                for (Proveedor p : providers) {
                    callback.onProgress("Subiendo proveedor: " + p.getNombreEmpresa());
                    
                    // Verificamos si es una actualización (si existiera una marca de server_id o si decidimos por lógica de negocio)
                    // Como no hay server_id numérico, usamos el teléfono como clave.
                    // Intentamos crear, si falla el servidor podría manejar el conflicto o usamos PUT.
                    
                    Response<GenericResponse> resp = userApi.createProveedorCompleto(p).execute();
                    if (resp.isSuccessful()) {
                        AppLogger.i("Proveedor " + p.getNombreEmpresa() + " subido con éxito.");
                        p.setSynced(true);
                        db.proveedorDao().update(p);
                        count++;
                    } else if (resp.code() == 400 || resp.code() == 409) {
                        // Posible conflicto de teléfono, intentamos actualización
                        Response<GenericResponse> respPut = userApi.updateProveedorCompleto(p.getTelefono(), p).execute();
                        if (respPut.isSuccessful()) {
                            AppLogger.i("Proveedor " + p.getNombreEmpresa() + " actualizado con éxito.");
                            p.setSynced(true);
                            db.proveedorDao().update(p);
                            count++;
                        } else {
                            AppLogger.e("Fallo al subir/actualizar proveedor " + p.getTelefono() + ". Error: " + respPut.code(), null);
                        }
                    } else {
                        AppLogger.e("Fallo al subir proveedor " + p.getTelefono() + ". Error: " + resp.code(), null);
                    }
                }

                AppLogger.i("--- PUSH FINALIZADO. Total sincronizados: " + count + " ---");
                callback.onSuccess("Sincronización completada. Se enviaron " + count + " registros.");
            } catch (Exception e) {
                AppLogger.e("ERROR CRÍTICO EN PUSH", e);
                callback.onError("Fallo al mandar datos: " + e.getMessage());
            }
        });
    }

    public void checkForServerChanges(SyncCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Integer lastProdId = db.productoDao().getMaxServerId();
                Response<ProductSyncResponse> respP = inventoryApi.getSyncProductos(lastProdId).execute();
                if (respP.isSuccessful() && respP.body() != null && respP.body().getProductos() != null && !respP.body().getProductos().isEmpty()) {
                    AppLogger.i("Notificación: Cambios detectados en el servidor.");
                    callback.onSuccess("Productos");
                }
            } catch (Exception ignored) {}
        });
    }

    public void correctFormats(SyncCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppLogger.d("Corrigiendo formatos locales...");
            List<Producto> products = db.productoDao().getUnsyncedProductos();
            for (Producto p : products) {
                if (p.getPresentacion() == null || p.getPresentacion().isEmpty()) {
                    p.setPresentacion("Unidad");
                    db.productoDao().update(p);
                }
            }
            callback.onSuccess("Formatos corregidos.");
        });
    }
}
