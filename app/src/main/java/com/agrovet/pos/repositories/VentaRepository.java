package com.agrovet.pos.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.agrovet.pos.AgrovetApplication;
import com.agrovet.pos.database.AppDatabase;
import com.agrovet.pos.database.VentaDao;
import com.agrovet.pos.models.Venta;
import java.util.List;

public class VentaRepository {
    private final VentaDao ventaDao;

    public VentaRepository(Application application) {
        AppDatabase db = ((AgrovetApplication) application).getDatabase();
        ventaDao = db.ventaDao();
    }

    public LiveData<List<Venta>> getAllVentas() {
        return ventaDao.getAllVentas();
    }

    public void insert(Venta venta) {
        AppDatabase.databaseWriteExecutor.execute(() -> ventaDao.insert(venta));
    }

    public void deleteById(long id) {
        AppDatabase.databaseWriteExecutor.execute(() -> ventaDao.deleteById(id));
    }
}
