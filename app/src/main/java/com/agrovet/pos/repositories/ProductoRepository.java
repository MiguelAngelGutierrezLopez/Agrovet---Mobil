package com.agrovet.pos.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.agrovet.pos.AgrovetApplication;
import com.agrovet.pos.database.AppDatabase;
import com.agrovet.pos.database.ProductoDao;
import com.agrovet.pos.models.Producto;
import com.agrovet.pos.network.InventoryApiService;
import com.agrovet.pos.network.RetrofitClient;
import com.agrovet.pos.utils.AppLogger;
import com.agrovet.pos.utils.DebugLog;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductoRepository {
    private final ProductoDao productoDao;
    private final InventoryApiService apiService;

    public ProductoRepository(Application application) {
        AppDatabase db = ((AgrovetApplication) application).getDatabase();
        productoDao = db.productoDao();
        apiService = RetrofitClient.getInventarioClient().create(InventoryApiService.class);
    }

    public LiveData<List<Producto>> getProductos() {
        return productoDao.getAllProductos();
    }

    public void insert(Producto producto) {
        producto.setSynced(false);
        DebugLog.sql("producto", "INSERT (Local)", producto.getNombre());
        AppDatabase.databaseWriteExecutor.execute(() -> {
            productoDao.insert(producto);
            // Ya no sincronizamos inmediatamente, se hará al final del día
        });
    }

    public void refreshProductos() {
        DebugLog.api("GET api/productos", "Iniciando (Carga Total)", null);
        apiService.getProductos(1000).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean success = Boolean.TRUE.equals(response.body().get("success"));
                    if (success) {
                        List<Map<String, Object>> productosRaw = (List<Map<String, Object>>) response.body().get("productos");
                        int count = (productosRaw != null) ? productosRaw.size() : 0;
                        DebugLog.api("GET api/productos", "EXITO", "Recibidos: " + count);
                        
                        if (productosRaw != null) {
                            AppDatabase.databaseWriteExecutor.execute(() -> {
                                for (Map<String, Object> map : productosRaw) {
                                    Producto p = new Producto();
                                    p.setServerId(map.get("id") != null ? ((Double) map.get("id")).intValue() : null);
                                    p.setNombre(String.valueOf(map.get("nombre")));
                                    p.setCategoria(String.valueOf(map.get("categoria")));
                                    p.setCantidad(map.get("cantidad") != null ? ((Double) map.get("cantidad")).intValue() : 0);
                                    p.setPrecioVenta(map.get("precio_venta") != null ? ((Double) map.get("precio_venta")).intValue() : 0);
                                    p.setPrecioCosto(map.get("precio_compra") != null ? ((Double) map.get("precio_compra")).intValue() : (map.get("precio_costo") != null ? ((Double) map.get("precio_costo")).intValue() : 0));
                                    p.setDescripcion(map.get("descripcion") != null ? String.valueOf(map.get("descripcion")) : "");
                                    p.setPresentacion(map.get("unidad") != null ? String.valueOf(map.get("unidad")) : "");
                                    p.setSynced(true);
                                    productoDao.insert(p);
                                }
                            });
                        }
                    } else {
                        DebugLog.api("GET api/productos", "FALLO_API", response.body().get("message"));
                    }
                } else {
                    DebugLog.api("GET api/productos", "ERROR_HTTP " + response.code(), null);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                DebugLog.error("Refresh Productos", t);
                AppLogger.e("Error al refrescar productos", t);
            }
        });
    }

    public void update(Producto producto) {
        producto.setSynced(false);
        AppDatabase.databaseWriteExecutor.execute(() -> productoDao.update(producto));
    }

    public void deleteById(int id) {
        AppDatabase.databaseWriteExecutor.execute(() -> productoDao.deleteById(id));
    }
}
