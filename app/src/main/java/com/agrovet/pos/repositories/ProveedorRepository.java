package com.agrovet.pos.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.agrovet.pos.AgrovetApplication;
import com.agrovet.pos.database.AppDatabase;
import com.agrovet.pos.database.ProveedorDao;
import com.agrovet.pos.models.Proveedor;
import java.util.List;

public class ProveedorRepository {
    private final ProveedorDao proveedorDao;

    public ProveedorRepository(Application application) {
        AppDatabase db = ((AgrovetApplication) application).getDatabase();
        proveedorDao = db.proveedorDao();
    }

    public LiveData<List<Proveedor>> getProveedores() {
        return proveedorDao.getAllProveedores();
    }

    public void insert(Proveedor proveedor) {
        AppDatabase.databaseWriteExecutor.execute(() -> proveedorDao.insert(proveedor));
    }

    public void update(Proveedor proveedor) {
        AppDatabase.databaseWriteExecutor.execute(() -> proveedorDao.update(proveedor));
    }

    public void deleteById(String id) {
        AppDatabase.databaseWriteExecutor.execute(() -> proveedorDao.deleteById(id));
    }
}
