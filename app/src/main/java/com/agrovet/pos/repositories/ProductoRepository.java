package com.agrovet.pos.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.agrovet.pos.AgrovetApplication;
import com.agrovet.pos.database.AppDatabase;
import com.agrovet.pos.database.ProductoDao;
import com.agrovet.pos.models.Producto;
import java.util.List;

public class ProductoRepository {
    private final ProductoDao productoDao;

    public ProductoRepository(Application application) {
        AppDatabase db = ((AgrovetApplication) application).getDatabase();
        productoDao = db.productoDao();
    }

    public LiveData<List<Producto>> getProductos() {
        return productoDao.getAllProductos();
    }

    public void insert(Producto producto) {
        AppDatabase.databaseWriteExecutor.execute(() -> productoDao.insert(producto));
    }

    public void update(Producto producto) {
        AppDatabase.databaseWriteExecutor.execute(() -> productoDao.update(producto));
    }

    public void deleteById(int id) {
        AppDatabase.databaseWriteExecutor.execute(() -> productoDao.deleteById(id));
    }
}
