package com.agrovet.pos.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.agrovet.pos.AgrovetApplication;
import com.agrovet.pos.database.AppDatabase;
import com.agrovet.pos.database.VentaDao;
import com.agrovet.pos.database.VentaItemDao;
import com.agrovet.pos.models.Venta;
import com.agrovet.pos.models.VentaItem;
import com.agrovet.pos.models.CartItem;
import com.agrovet.pos.network.RetrofitClient;
import com.agrovet.pos.network.SalesApiService;
import com.agrovet.pos.utils.AppLogger;
import com.agrovet.pos.utils.DebugLog;
import com.agrovet.pos.network.dto.SaleSyncResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VentaRepository {
    private final VentaDao ventaDao;
    private final VentaItemDao ventaItemDao;
    private final SalesApiService apiService;

    public VentaRepository(Application application) {
        AppDatabase db = ((AgrovetApplication) application).getDatabase();
        ventaDao = db.ventaDao();
        ventaItemDao = db.ventaItemDao();
        apiService = RetrofitClient.getVentasClient().create(SalesApiService.class);
    }

    public LiveData<List<Venta>> getAllVentas() {
        return ventaDao.getAllVentas();
    }

    public void insert(Venta venta, List<CartItem> items) {
        venta.setSynced(false);
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long id = ventaDao.insert(venta);
            List<VentaItem> ventaItems = new ArrayList<>();
            for (CartItem item : items) {
                ventaItems.add(new VentaItem((int) id, item));
            }
            ventaItemDao.insertAll(ventaItems);
        });
    }

    public void deleteById(long id) {
        AppDatabase.databaseWriteExecutor.execute(() -> ventaDao.deleteById(id));
    }

    public void getItemsByVenta(int ventaId, OnItemsLoadedCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<VentaItem> items = ventaItemDao.getItemsByVenta(ventaId);
            callback.onLoaded(items);
        });
    }

    public void filtrarVentas(String inicio, String fin, String tipo, String cliente, FilterCallback callback) {
        apiService.filtrarHistorial(inicio, fin, tipo, cliente).enqueue(new Callback<SaleSyncResponse>() {
            @Override
            public void onResponse(Call<SaleSyncResponse> call, Response<SaleSyncResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(response.body().getVentas());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<SaleSyncResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface FilterCallback {
        void onResult(List<Venta> ventas);
        void onError(String message);
    }

    public interface OnItemsLoadedCallback {
        void onLoaded(List<VentaItem> items);
    }
}
